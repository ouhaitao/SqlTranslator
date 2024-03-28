package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.CommonVisitor;
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

  private StringBuilder sqlBuilder;

  private Update update;

  public DMUpdateVisitor(final StringBuilder sqlBuilder, final Update update) {
    this.sqlBuilder = sqlBuilder;
    this.update = update;
  }

  public void visitor() {
    final ArrayList<UpdateSet> updateSets = update.getUpdateSets();
    sqlBuilder.append("UPDATE ");
    final Table table = update.getTable();
    if (table != null) {
      sqlBuilder.append(CommonVisitor.dealKeyword(table.getName().toUpperCase())).append(" ");
      if (table.getAlias() != null) {
        sqlBuilder.append(table.getAlias());
      }
    }
    visitorUpdateSets(updateSets);
    final Expression where = update.getWhere();
    if (where != null) {
      sqlBuilder.append(" WHERE ");
      where.accept(DMExpressionVisitor.getEnd(sqlBuilder));
    }
  }

  private void visitorUpdateSets(final ArrayList<UpdateSet> updateSets) {
    if (updateSets != null && !updateSets.isEmpty()) {
      sqlBuilder.append("SET ");
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
              dmExpressionVisitor = DMExpressionVisitor.getEnd(sqlBuilder);
            } else {
              dmExpressionVisitor = DMExpressionVisitor.getNotEnd(sqlBuilder);
            }
            final Column column = columns.get(i1);
            column.accept(dmExpressionVisitor);
            sqlBuilder.append("=");
            final Expression expression = expressions.get(i1);
            expression.accept(dmExpressionVisitor);
          }
        }
        if (i != size) {
          sqlBuilder.append(", ");
        }
      }
    }
  }
}
