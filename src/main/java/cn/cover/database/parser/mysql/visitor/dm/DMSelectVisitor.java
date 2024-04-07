package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMInsertVisitor.DMItemsListVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.BooleanConsumer;
import cn.cover.database.parser.mysql.visitor.dm.support.CommonVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.FunctionConverter;
import cn.cover.database.parser.mysql.visitor.dm.support.LimitVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlEnum;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil;
import cn.cover.database.parser.mysql.visitor.dm.support.StringUtil;
import cn.cover.database.parser.mysql.visitor.dm.support.TablePreExtract;
import java.util.Collection;
import java.util.List;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SetOperationList.SetOperationType;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 17:13
 */
public class DMSelectVisitor extends SelectVisitorAdapter {

  private final SqlAppender sqlBuilder;

  private Context context;

  public DMSelectVisitor(final SqlAppender sqlBuilder) {
    this.sqlBuilder = sqlBuilder;
  }

  public DMSelectVisitor(final Context context) {
    sqlBuilder = context.sqlBuild();
    this.context = context;
  }

  @Override
  public void visit(final PlainSelect plainSelect) {
    SqlEnum.SELECT.append(sqlBuilder);

    new TablePreExtract(context).extract(plainSelect);

    final Distinct distinct = plainSelect.getDistinct();
    if (distinct != null) {
      SqlEnum.DISTINCT.append(sqlBuilder);
    }

    final List<SelectItem> selectItems = plainSelect.getSelectItems();
    for (int i = 0, size = selectItems.size(); i < size; i++) {
      final SelectItem selectItem = selectItems.get(i);
      if (i != (size - 1)) {
        selectItem.accept(new DMSelectItemVisitor(context, false));
      } else {
        selectItem.accept(new DMSelectItemVisitor(context, true));
      }
    }

    // from table
    final FromItem fromItem = plainSelect.getFromItem();
    if (fromItem != null) {
      fromItem.accept(new DMFromItemVisitor(context));
    }

    selectJoins(plainSelect);

    final Expression where = plainSelect.getWhere();
    if (where != null) {
      SqlEnum.WHERE.append(sqlBuilder);
      where.accept(DMExpressionVisitor.getEnd(context));
    }

    final List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
    visitOrderBy(orderByElements);

    final Limit limit = plainSelect.getLimit();
    if (limit != null) {
      LimitVisitor.visit(limit, sqlBuilder);
    }
  }

  private void selectJoins(PlainSelect plainSelect) {
    final List<Join> joins = plainSelect.getJoins();
    if (joins != null && !joins.isEmpty()) {
      for (final Join join : joins) {
        if (join.isLeft()) {
          SqlEnum.LEFT_JOIN.append(sqlBuilder);
        } else if (join.isRight()) {
          SqlEnum.RIGHT_JOIN.append(sqlBuilder);
        } else if (join.isInner()) {
          SqlEnum.INNER_JOIN.append(sqlBuilder);
        } else if (join.isFull()) {
          SqlEnum.FULL_JOIN.append(sqlBuilder);
        } else {
          SqlEnum.JOIN.append(sqlBuilder);
        }

        final FromItem rightItem = join.getRightItem();
        if (rightItem instanceof Table) {
          Table table = (Table) rightItem;
          sqlBuilder.append(SqlUtil.appendTableName(table));
          SqlEnum.ON.append(sqlBuilder);
        } else if (rightItem instanceof SubSelect) {
          SubSelect subSelect = (SubSelect) rightItem;
          SubSelectVisitor.visit(subSelect, context);
          SqlEnum.ON.append(sqlBuilder);
        }

        final Collection<Expression> onExpressions = join.getOnExpressions();
        SqlUtil.expressionListVisitor(onExpressions, context);
      }
    }
  }

  private void visitOrderBy(final List<OrderByElement> orderByElements) {
    if (orderByElements != null && !orderByElements.isEmpty()) {
      SqlEnum.ORDER.append(sqlBuilder);
      SqlEnum.BY.append(sqlBuilder);
      for (int i = 0, size = (orderByElements.size() - 1); i <= size; i++) {
        final OrderByElement orderByElement = orderByElements.get(i);
        final Expression expression = orderByElement.getExpression();
        final boolean desc = !orderByElement.isAsc();
        final DMExpressionVisitor notEnd = DMExpressionVisitor.getNotEnd(context);
        final DMExpressionVisitor end = DMExpressionVisitor.getEnd(context);
        boolean last = (i == size);
        if (desc && !last) {
          expression.accept(end);
          SqlEnum.DESC.append(sqlBuilder);
          SqlEnum.COMMA.append(sqlBuilder);
        }
        if (desc && last) {
          expression.accept(end);
          SqlEnum.DESC.append(sqlBuilder);
        }
        if (!desc && !last) {
          expression.accept(notEnd);
        }
        if (!desc && last) {
          expression.accept(end);
        }
      }
    }
  }


  @Override
  public void visit(final SetOperationList setOpList) {
    final List<SelectBody> selects = setOpList.getSelects();
    final List<Boolean> brackets = setOpList.getBrackets();
    final List<SetOperation> operations = setOpList.getOperations();
    for (int i = 0, size = (selects.size() - 1); i <= size; i++) {
      final SelectBody selectBody = selects.get(i);
      final Boolean b = brackets.get(i);
      if (b) {
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      }
      selectBody.accept(new DMSelectVisitor(context));
      if (b) {
        SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      }

      if (i != size && operations!= null && !operations.isEmpty()) {
        final String setOperation = operations.get(i).toString();
        if (SetOperationType.UNION.name().equals(setOperation)) {
          // UNION ALL 不会进行数据类型比较，它只是简单地将两个结果集合并在一起。
          SqlEnum.UNION_ALL.append(sqlBuilder);
        } else {
          sqlBuilder.append(setOperation);
          SqlEnum.WHITE_SPACE.append(sqlBuilder);
        }
      }
    }
  }

  @Override
  public void visit(final WithItem withItem) {
    super.visit(withItem);
  }

  @Override
  public void visit(final ValuesStatement aThis) {
    super.visit(aThis);
  }

  static class DMSelectItemVisitor extends SelectItemVisitorAdapter {

    private final SqlAppender sqlBuilder;

    private final boolean lastOne;

    private final Context context;

    public DMSelectItemVisitor(final Context context, boolean lastOne) {
      this.sqlBuilder = context.sqlBuild();
      this.context = context;
      this.lastOne = lastOne;
    }

    @Override
    public void visit(final SelectExpressionItem item) {
      final Expression expression = item.getExpression();
      DMExpressionVisitor end = DMExpressionVisitor.getEnd(context);
      DMExpressionVisitor notEnd = DMExpressionVisitor.getNotEnd(context);
      final Alias alias = item.getAlias();
      if (!lastOne && alias != null) {
        expression.accept(end);
        SqlEnum.AS.append(sqlBuilder);
        sqlBuilder.append(alias.getName().replace(SqlUtil.BACKTICK, ""));
        SqlEnum.COMMA.append(sqlBuilder);
      }

      if (lastOne && alias != null) {
        expression.accept(end);
        SqlEnum.AS.append(sqlBuilder);
        sqlBuilder.append(alias.getName().replace(SqlUtil.BACKTICK, ""));
      }

      if (alias == null && !lastOne) {
        expression.accept(notEnd);
      }

      if (alias == null && lastOne) {
        expression.accept(end);
      }
    }

    @Override
    public void visit(final AllColumns columns) {
      columns.accept((ExpressionVisitor) DMExpressionVisitor.getEnd(context));
    }

    @Override
    public void visit(final AllTableColumns columns) {
      ExpressionVisitor end = DMExpressionVisitor.getEnd(context);
      ExpressionVisitor notEnd = DMExpressionVisitor.getNotEnd(context);
      if (lastOne) {
        columns.accept(end);
      } else {
        columns.accept(notEnd);
      }
    }
  }


  public static class DMExpressionVisitor extends ExpressionVisitorAdapter {

    private SqlAppender sqlBuilder;

    private final boolean lastOne;

    private Context context;

    public DMExpressionVisitor(final Context context, boolean lastOne) {
      this.lastOne = lastOne;
      this.context = context;
      if (context != null) {
        sqlBuilder = context.sqlBuild();
      }
    }

    public static DMExpressionVisitor getNotEnd(SqlAppender builder) {
      DMExpressionVisitor dmExpressionVisitor = new DMExpressionVisitor(null, false);
      dmExpressionVisitor.sqlBuilder = builder;
      return dmExpressionVisitor;
    }

    public static DMExpressionVisitor getNotEnd(Context context) {
      DMExpressionVisitor dmExpressionVisitor = new DMExpressionVisitor(null, false);
      dmExpressionVisitor.sqlBuilder = context.sqlBuild();
      dmExpressionVisitor.context = context;
      return dmExpressionVisitor;
    }


    public static DMExpressionVisitor getEnd(SqlAppender builder) {
      DMExpressionVisitor dmExpressionVisitor = new DMExpressionVisitor(null, true);
      dmExpressionVisitor.sqlBuilder = builder;
      return dmExpressionVisitor;
    }

    public static DMExpressionVisitor getEnd(Context context) {
      DMExpressionVisitor dmExpressionVisitor = new DMExpressionVisitor(null, true);
      dmExpressionVisitor.sqlBuilder = context.sqlBuild();
      dmExpressionVisitor.context = context;
      return dmExpressionVisitor;
    }

    @Override
    public void visit(final CaseExpression expr) {
      final List<WhenClause> whenClauses = expr.getWhenClauses();
      SqlEnum.CASE.append(sqlBuilder);
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      Expression switchExpression = expr.getSwitchExpression();
      boolean usingBrackets = expr.isUsingBrackets();
      if (usingBrackets) {
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      }
      if (switchExpression != null) {
        switchExpression.accept(dmExpressionVisitor);
      }
      if (whenClauses != null && !whenClauses.isEmpty()) {
        for (final WhenClause whenClause : whenClauses) {
          whenClause.accept(dmExpressionVisitor);
        }
      }

      final Expression elseExpression = expr.getElseExpression();
      if (elseExpression != null) {
        SqlEnum.ELSE.append(sqlBuilder);
        elseExpression.accept(dmExpressionVisitor);
        SqlEnum.WHITE_SPACE.append(sqlBuilder);
        SqlEnum.END.append(sqlBuilder);
      } else {
        SqlEnum.END.append(sqlBuilder);
      }
      if (usingBrackets) {
        SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      }
    }

    @Override
    public void visit(WhenClause expr) {
      Expression whenExpression = expr.getWhenExpression();
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      SqlEnum.WHEN.append(sqlBuilder);
      whenExpression.accept(dmExpressionVisitor);
      SqlEnum.WHITE_SPACE.append(sqlBuilder);

      Expression thenExpression = expr.getThenExpression();
      if (thenExpression != null) {
        SqlEnum.THEN.append(sqlBuilder);
        thenExpression.accept(dmExpressionVisitor);
        SqlEnum.WHITE_SPACE.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final CastExpression expr) {
      SqlEnum.CAST.append(sqlBuilder,false);
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      Expression leftExpression = expr.getLeftExpression();
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      if (leftExpression != null) {
        leftExpression.accept(dmExpressionVisitor);
      }
      ColDataType type = expr.getType();
      if (type != null) {
        SqlEnum.WHITE_SPACE.append(sqlBuilder);
        // TODO 类型转换
        String dataType = type.getDataType();
        if ("int".equalsIgnoreCase(dataType)) {
          dataType = "INTEGER";
        }
        if ("bool".equalsIgnoreCase(dataType)) {
          dataType = "BOOLEAN";
        }
        SqlEnum.AS.append(sqlBuilder);
        sqlBuilder.appendClose(dataType.toUpperCase());
      }
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }

    @Override
    public void visit(final Division expr) {
      Expression leftExpression = expr.getLeftExpression();
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      if (leftExpression != null) {
        leftExpression.accept(dmExpressionVisitor);
      }
      SqlEnum.DIVISION.append(sqlBuilder);
      Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(dmExpressionVisitor);
      }
    }

    @Override
    public void visit(final Column column) {
      final String prefixForColumn = SqlUtil.tablePrefixForColumn(column.getTable(), context);
      if (prefixForColumn != null && !prefixForColumn.isEmpty()) {
        sqlBuilder.appendClose(prefixForColumn);
      }

      String columnName = column.getColumnName().trim();
      boolean upper = !columnName.startsWith(SqlUtil.QUOTE) && !columnName.startsWith(SqlUtil.DOUBLE_QUOTE);
        if (columnName.contains(SqlUtil.BACKTICK)) {
        columnName = columnName.replaceAll(SqlUtil.BACKTICK, SqlUtil.BLANK);
      }

      if (upper) {
        String keyword = CommonVisitor.dealKeyword(columnName.toUpperCase());
        BooleanConsumer.newInstance(lastOne).accept(sqlBuilder::append, sqlBuilder::append, keyword);
      } else {
        BooleanConsumer.newInstance(lastOne).accept(sqlBuilder::append, sqlBuilder::append, columnName);
      }

      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final LongValue value) {
      sqlBuilder.append(value.getValue());
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(DoubleValue value) {
      sqlBuilder.append(value.getValue());
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
      super.visit(timeKeyExpression);
      String stringValue = timeKeyExpression.getStringValue();
      if (StringUtil.isNotBlank(stringValue)) {
        sqlBuilder.append(stringValue);
      }
    }

    @Override
    public void visit(final AllColumns allColumns) {
      SqlEnum.ASTERISK.append(sqlBuilder);
    }

    @Override
    public void visit(final Concat expr) {
      super.visit(expr);
    }

    @Override
    public void visit(final AllTableColumns allTableColumns) {
      final Table table = allTableColumns.getTable();

      final String prefixForColumn = SqlUtil.tablePrefixForColumn(table, context);
      if (prefixForColumn != null && !prefixForColumn.isEmpty()) {
        sqlBuilder.appendClose(prefixForColumn);
      }

      //final String tableName = table.getName();
      //final Collection<String> tableAlias = context.getContext().getTableAlias(tableName);
      //if (tableAlias != null && !tableAlias.isEmpty()) {
      //  sqlBuilder.appendClose(CommonVisitor.dealKeyword(tableName.toUpperCase()));
      //  SqlEnum.DOT.append(sqlBuilder);
      //  SqlEnum.ASTERISK.append(sqlBuilder);
      //  return;
      //}
      //sqlBuilder.appendClose(tableName);
      //SqlEnum.DOT.append(sqlBuilder);
      SqlEnum.ASTERISK.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final IsDistinctExpression isDistinctExpression) {
      super.visit(isDistinctExpression);
    }

    @Override
    public void visit(final StringValue value) {
      SqlEnum.QUOTE.append(sqlBuilder);
      sqlBuilder.appendClose(value.getValue());
      SqlEnum.QUOTE.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final NullValue expr) {
      SqlEnum.NULL.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final Function function) {

      boolean convert = FunctionConverter.convert(function, context);
      if (convert) {
        return;
      }

      final String upperCase = function.getName().toUpperCase();
      sqlBuilder.appendClose(upperCase);
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);

      if (function.isDistinct()) {
        SqlEnum.DISTINCT.append(sqlBuilder);
      }
      final ExpressionList parameters = function.getParameters();
      if (parameters != null) {
        final List<Expression> expressions = parameters.getExpressions();
        expressionListVisitor(expressions, sqlBuilder);
      }
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
      SqlEnum.QUESTION_MARK.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(AndExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.AND.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final InExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.IN.append(sqlBuilder);
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);

      final ItemsList rightItemsList = expr.getRightItemsList();
      if (rightItemsList != null) {
        rightItemsList.accept(new DMItemsListVisitor(sqlBuilder));
      }

      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      }
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }

    @Override
    public void visit(ExpressionList expressionList) {
      List<Expression> expressions = expressionList.getExpressions();
      if (expressionList.isUsingBrackets()) {
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      }
      expressionListVisitor(expressions, sqlBuilder);
      if (expressionList.isUsingBrackets()) {
        SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final EqualsTo expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.EQUALS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final SubSelect subSelect) {
      SubSelectVisitor.visit(subSelect, context);
    }

    @Override
    public void visit(final LikeExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      }
      if (expr.isNot()) {
        SqlEnum.NOT.append(sqlBuilder);
      }
      SqlEnum.LIKE.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      }
    }

    @Override
    public void visit(final IsNullExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      }
      SqlEnum.IS.append(sqlBuilder);
      if (expr.isNot()) {
        SqlEnum.NOT.append(sqlBuilder);
      }
      SqlEnum.NULL.append(sqlBuilder);
    }

    @Override
    public void visit(IntegerDivision expr) {
      Expression leftExpression = expr.getLeftExpression();
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      if (leftExpression != null) {
        leftExpression.accept(dmExpressionVisitor);
      }
      SqlEnum.DIV.append(sqlBuilder);
      Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(dmExpressionVisitor);
      }
    }

    @Override
    public void visit(IntervalExpression expr) {
      SqlEnum.INTERVAL.append(sqlBuilder);
      String parameter = expr.getParameter();
      if (StringUtil.isNotBlank(parameter)) {
        SqlEnum.QUOTE.append(sqlBuilder);
        sqlBuilder.appendClose(parameter);
        SqlEnum.QUOTE.append(sqlBuilder);
        SqlEnum.WHITE_SPACE.append(sqlBuilder);
      }
      String intervalType = expr.getIntervalType();
      if (StringUtil.isNotBlank(intervalType)) {
        sqlBuilder.appendClose(intervalType);
      }
    }

    @Override
    public void visit(Between expr) {
      super.visit(expr);
      if (expr.isNot()) {
        SqlEnum.NOT.append(sqlBuilder);
      }
      Expression leftExpression = expr.getLeftExpression();
      DMExpressionVisitor dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
      if (leftExpression != null) {
        leftExpression.accept(dmExpressionVisitor);
      }
      SqlEnum.BETWEEN.append(sqlBuilder);
      Expression betweenExpressionStart = expr.getBetweenExpressionStart();
      if (betweenExpressionStart != null) {
        betweenExpressionStart.accept(dmExpressionVisitor);
      }
      Expression betweenExpressionEnd = expr.getBetweenExpressionEnd();
      if (betweenExpressionEnd != null) {
        SqlEnum.AND.append(sqlBuilder);
        betweenExpressionEnd.accept(dmExpressionVisitor);
      }
    }

    @Override
    public void visit(final MySQLGroupConcat groupConcat) {
      // 正对mysql的GROUP_CONCAT转成达梦WM_CONCAT
      sqlBuilder.append("WM_CONCAT(");
      final ExpressionList expressionList = groupConcat.getExpressionList();
      final List<Expression> expressions = expressionList.getExpressions();
      expressionListVisitor(expressions, sqlBuilder);
      sqlBuilder.append(")");

      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    //
    public static void expressionListVisitor(final List<? extends Expression> expressions,
        SqlAppender sqlBuilder) {
      for (int i = 0, size = expressions.size(); i < size; i++) {
        final Expression expression = expressions.get(i);
        if (i != (size - 1)) {
          expression.accept(DMExpressionVisitor.getNotEnd(sqlBuilder));
        } else {
          expression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
        }
      }
    }

    @Override
    public void visit(final Parenthesis parenthesis) {
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      final Expression expression = parenthesis.getExpression();
      expression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }

    @Override
    public void visit(final Addition expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.ADD.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final Subtraction expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.SUB.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final Multiplication expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.ASTERISK.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final ExistsExpression expr) {
      SqlEnum.EXISTS.append(sqlBuilder);
      final Expression subSelect = expr.getRightExpression();
      if (subSelect != null) {
        subSelect.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final MinorThan expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.LESS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final MinorThanEquals expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.LESS.append(sqlBuilder,false);
      SqlEnum.EQUALS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final GreaterThan expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.GREAT.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final GreaterThanEquals expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(context));
      }
      SqlEnum.GREAT.append(sqlBuilder,false);
      SqlEnum.EQUALS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      if (rightExpression != null) {
        rightExpression.accept(DMExpressionVisitor.getEnd(context));
      }
    }

    @Override
    public void visit(final OrExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.OR.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final NotEqualsTo expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      SqlEnum.NOT_EQUALS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }
  }

  static class DMFromItemVisitor extends FromItemVisitorAdapter {

    private final SqlAppender sqlBuilder;

    private final Context context;

    public DMFromItemVisitor(final Context context) {
      this.sqlBuilder = context.sqlBuild();
      this.context = context;
    }

    @Override
    public void visit(final Table table) {
      SqlEnum.FROM.append(sqlBuilder);
      sqlBuilder.append(SqlUtil.appendTableName(table));
    }

    @Override
    public void visit(final SubSelect subSelect) {
      SqlEnum.FROM.append(sqlBuilder);
      SubSelectVisitor.visit(subSelect, context);
    }

    @Override
    public void visit(final SubJoin subjoin) {
      super.visit(subjoin);
    }

    @Override
    public void visit(final LateralSubSelect lateralSubSelect) {
      super.visit(lateralSubSelect);
    }

    @Override
    public void visit(final ValuesList valuesList) {
      super.visit(valuesList);
    }

    @Override
    public void visit(final TableFunction valuesList) {
      super.visit(valuesList);
    }

    @Override
    public void visit(final ParenthesisFromItem aThis) {
      super.visit(aThis);
    }
  }

  static class SubSelectVisitor {

    static void visit(SubSelect subSelect, Context context) {
      final SelectBody selectBody = subSelect.getSelectBody();
      SqlEnum.LEFT_PARENTHESIS.append(context.sqlBuild());
      selectBody.accept(new DMSelectVisitor(context));
      SqlEnum.RIGHT_PARENTHESIS.append(context.sqlBuild());
      final Alias alias = subSelect.getAlias();
      if (alias != null) {
        context.sqlBuild().append(alias.getName());
      }
    }
  }
}
