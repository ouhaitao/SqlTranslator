package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 17:50
 */
public class DMInsertVisitor {
  final Insert insert;
  final StringBuilder sqlBuilder;

  public DMInsertVisitor(final Insert insert, final StringBuilder sqlBuild) {
    this.insert = insert;
    this.sqlBuilder = sqlBuild;
  }

  public void visitor() {
    final List<Column> columns = insert.getColumns();

    final Table table = insert.getTable();
    sqlBuilder.append("INSERT INTO").append(" ")
        .append(CommonVisitor.dealKeyword(table.getName().toUpperCase())).append(" ");
    if (table.getAlias() != null) {
      sqlBuilder.append(table.getAlias()).append(" ");
    }
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
