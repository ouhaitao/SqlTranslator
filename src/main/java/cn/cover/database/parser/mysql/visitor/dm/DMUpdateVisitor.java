package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.CommonVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlEnum;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/27 8:30
 */
public class DMUpdateVisitor {

  private SqlAppender sqlBuilder;

  private Update update;

  private Context context;

  public DMUpdateVisitor(final Context context, final Update update) {
    this.sqlBuilder = context.sqlBuild();
    this.update = update;
    this.context = context;
  }

  public void visitor() {
    final ArrayList<UpdateSet> updateSets = update.getUpdateSets();
    SqlEnum.UPDATE.append(sqlBuilder);
    final Table table = update.getTable();
    sqlBuilder.append(SqlUtil.appendTableName(table));
    //if (table != null) {
    //  sqlBuilder.append(CommonVisitor.dealKeyword(table.getName().toUpperCase())).append(" ");
    //  if (table.getAlias() != null) {
    //    sqlBuilder.append(table.getAlias());
    //  }
    //}
    visitorUpdateSets(updateSets);
    final Expression where = update.getWhere();
    if (where != null) {
      //sqlBuilder.append(" WHERE ");
      SqlEnum.WHERE.append(sqlBuilder);
      where.accept(DMExpressionVisitor.getEnd(context));
    }
  }

  private void visitorUpdateSets(final ArrayList<UpdateSet> updateSets) {
    if (updateSets != null && !updateSets.isEmpty()) {
      //sqlBuilder.append("SET ");
      SqlEnum.SET.append(sqlBuilder);
      for (int i = 0, size = (updateSets.size() - 1); i <= size; i++) {
        final UpdateSet updateSet = updateSets.get(i);
        final ArrayList<Column> columns = updateSet.getColumns();
        final ArrayList<Expression> expressions = updateSet.getExpressions();
        if (columns != null && !columns.isEmpty() && expressions != null
            && !expressions.isEmpty()) {
          int isize = Math.min(columns.size(), expressions.size()) - 1;
          for (int i1 = 0; i1 <= isize; i1++) {
            DMExpressionVisitor dmExpressionVisitor;
            if (i1 == isize) {
              dmExpressionVisitor = DMExpressionVisitor.getEnd(context);
            } else {
              dmExpressionVisitor = DMExpressionVisitor.getNotEnd(context);
            }
            final Column column = columns.get(i1);
            column.accept(dmExpressionVisitor);
            SqlEnum.EQUALS.append(sqlBuilder);
            //sqlBuilder.append("=");
            final Expression expression = expressions.get(i1);
            expression.accept(dmExpressionVisitor);
          }
        }
        if (i != size) {
          SqlEnum.COMMA.append(sqlBuilder);
          //sqlBuilder.append(", ");
        }
      }
    }
  }
}
