package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/27 9:20
 */
public class DMDeleteVisitor {

  private StringBuilder sqlBuilder;

  private Delete delete;

  public DMDeleteVisitor(final StringBuilder sqlBuilder, final Delete delete) {
    this.sqlBuilder = sqlBuilder;
    this.delete = delete;
  }

  public void visitor() {
    final Table table = delete.getTable();
    sqlBuilder.append("DELETE FROM ");
    if (table != null) {
      sqlBuilder.append(CommonVisitor.dealKeyword(table.getName().toUpperCase())).append(" ");
      if (table.getAlias() != null) {
        sqlBuilder.append(table.getAlias()).append(" ");
      }
    }
    final Expression where = delete.getWhere();
    if (where != null) {
      sqlBuilder.append("WHERE ");
      where.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }
  }
}
