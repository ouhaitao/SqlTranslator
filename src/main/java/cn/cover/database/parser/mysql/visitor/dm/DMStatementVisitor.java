package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 17:08
 */
public class DMStatementVisitor extends StatementVisitorAdapter {

  private final StringBuilder sqlBuilder;

  public DMStatementVisitor(final StringBuilder sqlBuilder) {
    this.sqlBuilder = sqlBuilder;
  }

  @Override
  public void visit(final Select select) {
    final SelectBody selectBody = select.getSelectBody();
    selectBody.accept(new DMSelectVisitor(sqlBuilder));
  }

  @Override
  public void visit(final Insert insert) {
    final List<Column> columns = insert.getColumns();

    sqlBuilder.append("INSERT INTO").append(" ").append(insert.getTable().getName());
    if (columns != null && !columns.isEmpty()) {
      sqlBuilder.append(" (");
      DMExpressionVisitor.expressionListVisitor(columns, sqlBuilder);
      sqlBuilder.append(") ");
    }
    sqlBuilder.append(" VALUES (");
    final ItemsList itemsList = insert.getItemsList();
    itemsList.accept(new DMItemsListVisitor(sqlBuilder));
    sqlBuilder.append(")");
  }

  static class DMItemsListVisitor extends ItemsListVisitorAdapter {

    private final StringBuilder stringBuilder;

    public DMItemsListVisitor(final StringBuilder stringBuilder) {
      this.stringBuilder = stringBuilder;
    }

    @Override
    public void visit(final ExpressionList expressionList) {
      final List<Expression> expressions = expressionList.getExpressions();
      DMExpressionVisitor.expressionListVisitor(expressions, stringBuilder);
    }
  }
}
