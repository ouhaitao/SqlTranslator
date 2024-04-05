package cn.cover.database.parser.mysql.visitor.dm.support;

import net.sf.jsqlparser.statement.select.Limit;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 10:15
 */
public class LimitVisitor {

  public static void visit(Limit limit, SqlAppender sqlBuilder) {
    SqlEnum.LIMIT.append(sqlBuilder);
    if (limit.getOffset() != null) {
      sqlBuilder.append(limit.getOffset());
    }
    if (limit.getOffset() != null && limit.getRowCount() != null) {
      SqlEnum.COMMA.append(sqlBuilder);
    }
    if (limit.getRowCount() != null) {
      sqlBuilder.append(limit.getRowCount());
    }
  }
}
