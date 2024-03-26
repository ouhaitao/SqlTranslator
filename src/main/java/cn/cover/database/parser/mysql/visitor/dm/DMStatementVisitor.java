package cn.cover.database.parser.mysql.visitor.dm;

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
    DMInsertVisitor dmInsertVisitor = new DMInsertVisitor(insert, sqlBuilder);
    dmInsertVisitor.visitor();
  }
}
