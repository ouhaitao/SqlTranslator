package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlEnum;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/27 9:20
 */
public class DMDeleteVisitor {

  private SqlAppender sqlBuilder;

  private final Delete delete;

  private Context context;

  public DMDeleteVisitor(final Context context, final Delete delete) {
    this.context = context;
    this.delete = delete;
    this.sqlBuilder = context.sqlBuild();
  }

  public void visitor() {
    final Table table = delete.getTable();
    //sqlBuilder.append("DELETE FROM ");
    SqlEnum.DELETE.append(sqlBuilder);
    SqlEnum.FROM.append(sqlBuilder);
    if (table != null) {
      //sqlBuilder.append(CommonVisitor.dealKeyword(table.getName().toUpperCase())).append(" ");
      //if (table.getAlias() != null) {
      //  sqlBuilder.append(table.getAlias()).append(" ");
      //}
      sqlBuilder.append(SqlUtil.appendTableName(table, context));
    }

    final Expression where = delete.getWhere();
    if (where != null) {
      //sqlBuilder.append("WHERE ");
      SqlEnum.WHERE.append(sqlBuilder);
      where.accept(DMExpressionVisitor.getEnd(context));
    }
  }
}
