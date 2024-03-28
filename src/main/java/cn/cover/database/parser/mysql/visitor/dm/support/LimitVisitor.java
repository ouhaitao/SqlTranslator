package cn.cover.database.parser.mysql.visitor.dm.support;

import net.sf.jsqlparser.statement.select.Limit;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 10:15
 */
public class LimitVisitor {

  public static void visit(Limit limit, StringBuilder sqlBuilder) {
    sqlBuilder.append(" LIMIT ");
    if (limit.getOffset() != null) {
      sqlBuilder.append(limit.getOffset());
    }
    if (limit.getOffset() != null && limit.getRowCount() != null) {
      sqlBuilder.append(", ");
    }
    if (limit.getRowCount() != null) {
      sqlBuilder.append(limit.getRowCount());
    }
  }
}
