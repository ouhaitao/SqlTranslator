package cn.cover.database.parser.mysql;

import cn.cover.database.Parser;
import cn.cover.database.sql.RawSQL;
import cn.cover.database.sql.SQL;
import cn.cover.exception.SqlTranslateException;
import java.util.Collection;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author parry 2024/03/26
 */
public class DMSqlParser implements Parser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DMSqlParser.class);

  @Override
  public SQL parse(String originSQL) throws SqlTranslateException {
    StringBuilder translateSql = new StringBuilder();
    try {
      Statement stmt = CCJSqlParserUtil.parse(originSQL);
      if (stmt instanceof Select) {
        Select select = (Select) stmt;
        select.accept(new DMStatementVisitorAdapter(translateSql));
        System.out.println(select);
      }
    } catch (JSQLParserException e) {
      LOGGER.info("J");
    }

    LOGGER.info("转换之后的sql: {}", translateSql);

    return new RawSQL(originSQL);
  }

  static class DMStatementVisitorAdapter extends StatementVisitorAdapter {

    private final StringBuilder sqlBuilder;

    public DMStatementVisitorAdapter(final StringBuilder sqlBuilder) {
      this.sqlBuilder = sqlBuilder;
    }

    @Override
    public void visit(final Select select) {
      final SelectBody selectBody = select.getSelectBody();
      selectBody.accept(new DMSelectVisitor(sqlBuilder));
    }
  }

  static class DMSelectVisitor extends SelectVisitorAdapter {

    private final StringBuilder sqlBuilder;

    public DMSelectVisitor(final StringBuilder sqlBuilder) {
      this.sqlBuilder = sqlBuilder;
    }

    @Override
    public void visit(final PlainSelect plainSelect) {
      sqlBuilder.append("SELECT ");

      // select items
      final List<SelectItem> selectItems = plainSelect.getSelectItems();
      for (int i = 0, size = selectItems.size(); i < size; i++) {
        final SelectItem selectItem = selectItems.get(i);
        if (i != (size - 1)) {
          selectItem.accept(DMExpressionVisitor.getNotEnd(sqlBuilder));
        } else {
          selectItem.accept(DMExpressionVisitor.getEnd(sqlBuilder));
        }
      }

      // from table
      final FromItem fromItem = plainSelect.getFromItem();
      fromItem.accept(new DMFromItemVisitor(sqlBuilder));

      final List<Join> joins = plainSelect.getJoins();
      for (final Join join : joins) {
        if (join.isLeft()) {
          sqlBuilder.append(" LEFT JOIN ");
        } else if (join.isRight()) {
          sqlBuilder.append(" RIGHT JOIN ");
        }
        final Collection<Expression> onExpressions = join.getOnExpressions();
        int i = 0;
        int size = onExpressions.size() - 1;
        for (final Expression onExpression : onExpressions) {
          if (i == size) {
            onExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
          } else {
            onExpression.accept(DMExpressionVisitor.getNotEnd(sqlBuilder));
          }
        }
      }

      final Expression where = plainSelect.getWhere();
      sqlBuilder.append(" WHERE ");
      where.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }
  }

  static class DMSelectItemVisitor extends SelectItemVisitorAdapter {

    private final StringBuilder sqlBuilder;

    private final boolean lastOne;

    public DMSelectItemVisitor(final StringBuilder sqlBuilder, boolean lastOne) {
      this.sqlBuilder = sqlBuilder;
      this.lastOne = lastOne;
    }

    public DMSelectItemVisitor(final StringBuilder sqlBuilder) {
      this(sqlBuilder, false);
    }

    @Override
    public void visit(final SelectExpressionItem item) {
      final Expression expression = item.getExpression();
      expression.accept(new DMExpressionVisitor(sqlBuilder, lastOne));
    }
  }


  static class DMExpressionVisitor extends ExpressionVisitorAdapter {

    private static final DMExpressionVisitor NOT_END = new DMExpressionVisitor(null, false);
    private static final DMExpressionVisitor END = new DMExpressionVisitor(null, true);

    private StringBuilder sqlBuilder;

    private final boolean lastOne;

    private boolean notUpper = true;

    public DMExpressionVisitor(final StringBuilder sqlBuilder, boolean lastOne) {
      this.sqlBuilder = sqlBuilder;
      this.lastOne = lastOne;
    }

    public DMExpressionVisitor(final StringBuilder sqlBuilder) {
      this(sqlBuilder, false);
    }

    public static DMExpressionVisitor getNotEnd(StringBuilder builder) {
      NOT_END.sqlBuilder = builder;
      return NOT_END;
    }

    public static DMExpressionVisitor getNotEnd(StringBuilder builder, boolean notUpper) {
      NOT_END.sqlBuilder = builder;
      NOT_END.notUpper = notUpper;
      return NOT_END;
    }

    public static DMExpressionVisitor getEnd(StringBuilder builder) {
      END.sqlBuilder = builder;
      return END;
    }

    public static DMExpressionVisitor getEnd(StringBuilder builder, boolean notUpper) {
      END.sqlBuilder = builder;
      END.notUpper = notUpper;
      return END;
    }

    @Override
    public void visit(final Column column) {
      //super.visit(column);
      //sqlBuilder.append(" ");
      if (column.getTable() != null) {
        sqlBuilder.append(column.getTable().getName()).append(".");
      }
      String columnName = column.getColumnName().trim();

      boolean upper = true;
      if (columnName.startsWith("'") || columnName.startsWith("\"")) {
        upper = false;
      }

      if (columnName.contains("`")) {
        columnName = columnName.replaceAll("`", "");
        if (columnName.contains(".")) {
          final String[] split = columnName.split("\\.", 2);
          sqlBuilder.append(split[0]).append(".");
          columnName = split[1];
        }
      }

      if (upper) {
        sqlBuilder.append(columnName.toUpperCase());
      } else {
        sqlBuilder.append(columnName);
      }

      if (!lastOne) {
        sqlBuilder.append(", ");
      }
    }

    @Override
    public void visit(final LongValue value) {
      sqlBuilder.append(value.getValue());
      if (!lastOne) {
        sqlBuilder.append(", ");
      }
    }

    @Override
    public void visit(final Function function) {
      sqlBuilder.append(function.getName().toUpperCase()).append("(");
      final ExpressionList parameters = function.getParameters();
      final List<Expression> expressions = parameters.getExpressions();
      for (int i = 0, size = expressions.size(); i < size; i++) {
        final Expression expression = expressions.get(i);
        if (i != (size - 1)) {
          expression.accept(DMExpressionVisitor.getNotEnd(sqlBuilder));
        } else {
          expression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
        }
      }
      sqlBuilder.append(")");

      if (!lastOne) {
        sqlBuilder.append(", ");
      }
    }

    @Override
    public void visit(AndExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      sqlBuilder.append(" and ");
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final InExpression expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      sqlBuilder.append(" IN (");
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      sqlBuilder.append(") ");
    }

    @Override
    public void visit(final EqualsTo expr) {
      final Expression leftExpression = expr.getLeftExpression();
      leftExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
      sqlBuilder.append(" = ");
      final Expression rightExpression = expr.getRightExpression();
      rightExpression.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }

    @Override
    public void visit(final SubSelect subSelect) {
      final SelectBody selectBody = subSelect.getSelectBody();
      // TODO
    }
  }

  static class DMFromItemVisitor extends FromItemVisitorAdapter {

    private final StringBuilder sqlBuilder;

    public DMFromItemVisitor(final StringBuilder sqlBuilder) {
      this.sqlBuilder = sqlBuilder;
    }

    @Override
    public void visit(final Table table) {
      sqlBuilder.append(" FROM ");
      sqlBuilder.append(table.getName()).append(" ");
      if (table.getAlias() != null) {
        sqlBuilder.append(table.getAlias().getName());
      }
    }

    @Override
    public void visit(final SubSelect subSelect) {
      super.visit(subSelect);
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

}
