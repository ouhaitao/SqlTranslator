package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 17:08
 */
public class DMStatementVisitor extends StatementVisitorAdapter {

  private final SqlAppender sqlBuilder;
  private final Context context;

  public DMStatementVisitor(final Context context) {
    sqlBuilder = context.sqlBuild();
    this.context = context;
  }

  @Override
  public void visit(final Select select) {
    final SelectBody selectBody = select.getSelectBody();
    selectBody.accept(new DMSelectVisitor(context));
  }

  @Override
  public void visit(final Insert insert) {
    DMInsertVisitor dmInsertVisitor = new DMInsertVisitor(insert, context);
    dmInsertVisitor.visitor();
  }

  @Override
  public void visit(final Update update) {
    new DMUpdateVisitor(context, update).visitor();
  }

  @Override
  public void visit(final Delete delete) {
    new DMDeleteVisitor(context, delete).visitor();
  }
}
