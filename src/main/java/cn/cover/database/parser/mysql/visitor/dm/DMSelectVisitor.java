package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.support.CommonVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.LimitVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlEnum;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil;
import cn.cover.database.parser.mysql.visitor.dm.support.TablePreExtract;
import java.util.Collection;
import java.util.List;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
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
    fromItem.accept(new DMFromItemVisitor(context));

    final List<Join> joins = plainSelect.getJoins();
    if (joins != null && !joins.isEmpty()) {
      for (final Join join : joins) {
        if (join.isLeft()) {
          //sqlBuilder.append(" LEFT JOIN ");
          SqlEnum.LEFT_JOIN.append(sqlBuilder);
        } else if (join.isRight()) {
          //sqlBuilder.append(" RIGHT JOIN ");
          SqlEnum.RIGHT_JOIN.append(sqlBuilder);
        } else if (join.isInner()) {
          //sqlBuilder.append(" INNER JOIN ");
          SqlEnum.INNER_JOIN.append(sqlBuilder);
        } else if (join.isFull()) {
          //sqlBuilder.append(" FULL JOIN ");
          SqlEnum.FULL_JOIN.append(sqlBuilder);
        }

        final FromItem rightItem = join.getRightItem();
        if (rightItem instanceof Table) {
          Table table = (Table) rightItem;
          //String tableName = table.getName().replaceAll("`", "");
          //sqlBuilder.append(CommonVisitor.dealKeyword(tableName.toUpperCase())).append(" ");
          //String tableAlias = "";
          //if (table.getAlias() != null) {
          //  tableAlias = table.getAlias().getName();
          //  sqlBuilder.append(tableAlias).append(" ");
          //}
          //context.putTable(tableName, tableAlias);
          sqlBuilder.append(SqlUtil.appendTableName(table, context));
          SqlEnum.ON.append(sqlBuilder);
        }

        final Collection<Expression> onExpressions = join.getOnExpressions();

        SqlUtil.expressionListVisitor(onExpressions, context);
        //int i = 0;
        //int size = onExpressions.size() - 1;
        //for (final Expression onExpression : onExpressions) {
        //  if (i == size) {
        //    onExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
        //  } else {
        //    onExpression.accept(DMExpressionVisitor.getNotEnd(sqlBuilder));
        //  }
        //}
      }
    }

    final Expression where = plainSelect.getWhere();
    if (where != null) {
      //sqlBuilder.append(" WHERE ");
      SqlEnum.WHERE.append(sqlBuilder);
      where.accept(DMExpressionVisitor.getEnd(context));
    }

    final Limit limit = plainSelect.getLimit();
    if (limit != null) {
      LimitVisitor.visit(limit, sqlBuilder);
    }

    final List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
    visitOrderBy(orderByElements);
  }

  private void visitOrderBy(final List<OrderByElement> orderByElements) {
    if (orderByElements != null && !orderByElements.isEmpty()) {
      sqlBuilder.append(" ORDER BY ");
      for (int i = 0, size = (orderByElements.size() - 1); i <= size; i++) {
        final OrderByElement orderByElement = orderByElements.get(i);
        final Expression expression = orderByElement.getExpression();
        final boolean desc = !orderByElement.isAsc();
        final DMExpressionVisitor notEnd = DMExpressionVisitor.getNotEnd(sqlBuilder);
        final DMExpressionVisitor end = DMExpressionVisitor.getEnd(sqlBuilder);
        boolean last = (i == size);
        if (desc && !last) {
          expression.accept(end);
          sqlBuilder.append(" DESC ").append(", ");
        }
        if (desc && last) {
          expression.accept(end);
          sqlBuilder.append(" DESC ");
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
        sqlBuilder.append(" ( ");
      }
      selectBody.accept(new DMSelectVisitor(sqlBuilder));
      if (b) {
        sqlBuilder.append(" ) ");
      }

      if (i != size && operations!= null && !operations.isEmpty()) {
        final String setOperation = operations.get(i).toString();
        if (SetOperationType.UNION.name().equals(setOperation)) {
          // UNION ALL 不会进行数据类型比较，它只是简单地将两个结果集合并在一起。
          sqlBuilder.append(" UNION ALL").append(" ");
        } else {
          sqlBuilder.append(setOperation).append(" ");
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
        sqlBuilder.append(alias.getName());
        SqlEnum.COMMA.append(sqlBuilder);
      }

      if (lastOne && alias != null) {
        expression.accept(end);
        SqlEnum.AS.append(sqlBuilder);
        sqlBuilder.append(alias.getName());
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
      columns.accept((ExpressionVisitor) DMExpressionVisitor.getEnd(context));
    }
  }


  public static class DMExpressionVisitor extends ExpressionVisitorAdapter {

    private static final DMExpressionVisitor NOT_END = new DMExpressionVisitor(null, false);
    private static final DMExpressionVisitor END = new DMExpressionVisitor(null, true);

    private SqlAppender sqlBuilder;

    private final boolean lastOne;

    private boolean notUpper = true;

    private Context context;

    //public DMExpressionVisitor(final StringBuilder sqlBuilder, boolean lastOne) {
    //  this.sqlBuilder = sqlBuilder;
    //  this.lastOne = lastOne;
    //}

    public DMExpressionVisitor(final Context context, boolean lastOne) {
      this.lastOne = lastOne;
      this.context = context;
      if (context != null) {
        sqlBuilder = context.sqlBuild();
      }
    }

    //public DMExpressionVisitor(final StringBuilder sqlBuilder) {
    //  this(sqlBuilder, false);
    //}

    public DMExpressionVisitor(final Context context) {
      this(context, false);
    }

    public static DMExpressionVisitor getNotEnd(SqlAppender builder) {
      NOT_END.sqlBuilder = builder;
      return NOT_END;
    }

    public static DMExpressionVisitor getNotEnd(Context context) {
      NOT_END.sqlBuilder = context.sqlBuild();
      NOT_END.context = context;
      return NOT_END;
    }

    public static DMExpressionVisitor getNotEnd(SqlAppender builder, boolean notUpper) {
      NOT_END.sqlBuilder = builder;
      NOT_END.notUpper = notUpper;
      return NOT_END;
    }

    public static DMExpressionVisitor getEnd(SqlAppender builder) {
      END.sqlBuilder = builder;
      return END;
    }

    public static DMExpressionVisitor getEnd(Context context) {
      END.sqlBuilder = context.sqlBuild();
      END.context = context;
      return END;
    }

    public static DMExpressionVisitor getEnd(SqlAppender builder, boolean notUpper) {
      END.sqlBuilder = builder;
      END.notUpper = notUpper;
      return END;
    }

    @Override
    public void visit(final Column column) {
      final String prefixForColumn = SqlUtil.tablePrefixForColumn(column.getTable(), context);
      if (prefixForColumn != null && !prefixForColumn.isEmpty()) {
        sqlBuilder.appendClose(prefixForColumn);
      }

      String columnName = column.getColumnName().trim();

      boolean upper = true;
      if (columnName.startsWith(SqlUtil.QUOTE) || columnName.startsWith(SqlUtil.DOUBLE_QUOTE)) {
        upper = false;
      }

      if (columnName.contains(SqlUtil.BACKTICK)) {
        columnName = columnName.replaceAll(SqlUtil.BACKTICK, SqlUtil.BLANK);
        //if (columnName.contains(".")) {
        //  final String[] split = columnName.split("\\.", 2);
        //  sqlBuilder.append(split[0]).append(".");
        //  columnName = split[1];
        //}
      }

      if (upper) {
        sqlBuilder.append(columnName.toUpperCase());
      } else {
        sqlBuilder.append(columnName);
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
    public void visit(final AllColumns allColumns) {
      SqlEnum.ASTERISK.append(sqlBuilder);
    }

    @Override
    public void visit(final AllTableColumns allTableColumns) {
      final Table table = allTableColumns.getTable();
      final String tableName = table.getName();
      final Collection<String> tableAlias = context.getContext().getTableAlias(tableName);
      if (tableAlias != null && !tableAlias.isEmpty()) {
        sqlBuilder.appendClose(CommonVisitor.dealKeyword(tableName.toUpperCase()));
        SqlEnum.DOT.append(sqlBuilder);
        SqlEnum.ASTERISK.append(sqlBuilder);
      }
      //sqlBuilder.append(" ").append(table.getName().toUpperCase()).append(".* ");
    }

    @Override
    public void visit(final StringValue value) {
      //sqlBuilder.append("'").append(value.getValue()).append("'");
      SqlEnum.QUOTE.append(sqlBuilder);
      sqlBuilder.appendClose(value.getValue());
      SqlEnum.QUOTE.append(sqlBuilder);
      if (!lastOne) {
        //sqlBuilder.append(", ");
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final NullValue expr) {
      //sqlBuilder.append("NULL");
      SqlEnum.NULL.append(sqlBuilder);
      if (!lastOne) {
        //sqlBuilder.append(", ");
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(final Function function) {
      //sqlBuilder.append(" ").append(function.getName().toUpperCase()).append("(");
      sqlBuilder.appendClose(function.getName().toUpperCase());
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      final ExpressionList parameters = function.getParameters();
      if (parameters != null) {
        final List<Expression> expressions = parameters.getExpressions();
        expressionListVisitor(expressions, sqlBuilder);
      }
      //sqlBuilder.append(")");
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
      //sqlBuilder.append("? ");
      SqlEnum.QUESTION_MARK.append(sqlBuilder);
      if (!lastOne) {
        SqlEnum.COMMA.append(sqlBuilder);
      }
    }

    @Override
    public void visit(AndExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" AND ");
      SqlEnum.AND.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final InExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" IN (");
      SqlEnum.IN.append(sqlBuilder);
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(") ");
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }

    @Override
    public void visit(final EqualsTo expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" = ");
      SqlEnum.Equals.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final SubSelect subSelect) {
      SubSelectVisitor.visit(subSelect, sqlBuilder);
    }

    @Override
    public void visit(final LikeExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      if (leftExpression != null) {
        leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      }
      if (expr.isNot()) {
        //sqlBuilder.append(" NOT");
        SqlEnum.NOT.append(sqlBuilder);
      }
      //sqlBuilder.append(" LIKE");
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
      //sqlBuilder.append(" IS ");
      if (expr.isNot()) {
        //sqlBuilder.append("NOT");
        SqlEnum.NOT.append(sqlBuilder);
      }
      //sqlBuilder.append("NULL");
      SqlEnum.NULL.append(sqlBuilder);
    }

    @Override
    public void visit(final Concat expr) {
      //super.visit(expr);
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
      //sqlBuilder.append(" (");
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      final Expression expression = parenthesis.getExpression();
      expression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" ) ");
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }

    @Override
    public void visit(final OrExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" OR ");
      SqlEnum.OR.append(sqlBuilder);
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final NotEqualsTo expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      //sqlBuilder.append(" != ");
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
      //sqlBuilder.append(" FROM ");
      //String tableName = table.getName().replaceAll("`", "");
      //sqlBuilder.append(CommonVisitor.dealKeyword(tableName.toUpperCase())).append(" ");
      //if (table.getAlias() != null) {
      //  sqlBuilder.append(table.getAlias().getName());
      //}
      sqlBuilder.append(SqlUtil.appendTableName(table, context));
    }

    @Override
    public void visit(final SubSelect subSelect) {
      //sqlBuilder.append(" FROM ");
      SqlEnum.SELECT.append(sqlBuilder);
      SubSelectVisitor.visit(subSelect, sqlBuilder);
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

    static void visit(SubSelect subSelect, SqlAppender sqlBuilder) {
      final SelectBody selectBody = subSelect.getSelectBody();
      //sqlBuilder.append(" (");
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      selectBody.accept(new DMSelectVisitor(sqlBuilder));
      //sqlBuilder.append(" ) ");
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      final Alias alias = subSelect.getAlias();
      if (alias != null) {
        sqlBuilder.append(alias.getName());
      }
    }
  }
}
