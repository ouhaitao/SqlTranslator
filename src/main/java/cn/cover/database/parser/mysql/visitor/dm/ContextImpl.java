package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import java.util.Collection;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 9:15
 */
public class ContextImpl implements Context {

  private ContextAttr attr;

  public static ContextImpl newInstance(String originSql) {
    final ContextImpl context = new ContextImpl();
    context.attr = new ContextAttr(originSql);
    return context;
  }

  @Override
  public ContextAttr getContext() {
    return this.attr;
  }

  @Override
  public void setContext(final ContextAttr c) {
    this.attr = c;
  }

  public boolean putTable(String table, String alias) {
    return attr.putTable(table, alias);
  }

  public Collection<String> getTableAlias(String table) {
    return attr.getTableAlias(table);
  }

  public String getOriginSql() {
    return attr.getOriginSql();
  }

  public SqlAppender getSqlBuilder() {
    return attr.getSqlAppender();
  }

  @Override
  public SqlAppender sqlBuild() {
    return this.attr.getSqlAppender();
  }
}
