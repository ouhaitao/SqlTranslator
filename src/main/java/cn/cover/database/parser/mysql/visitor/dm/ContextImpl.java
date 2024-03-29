package cn.cover.database.parser.mysql.visitor.dm;

import java.util.Collection;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 9:15
 */
public class ContextImpl implements Context {

  private ContextAttr contextAttr = new ContextAttr();

  @Override
  public ContextAttr getContext() {
    return this.contextAttr;
  }

  @Override
  public void setContext(final ContextAttr c) {
    this.contextAttr = c;
  }

  public boolean putTable(String table, String alias) {
    return contextAttr.putTable(table, alias);
  }

  public Collection<String> getTableAlias(String table) {
    return contextAttr.getTableAlias(table);
  }

  public String getOriginSql() {
    return contextAttr.getOriginSql();
  }

  public void setOriginSql(final String originSql) {
    this.contextAttr.setOriginSql(originSql);
  }

  public StringBuilder getSqlBuilder() {
    return contextAttr.getSqlBuilder();
  }

  public void setSqlBuilder(final StringBuilder sqlBuilder) {
    this.contextAttr.setSqlBuilder(sqlBuilder);
  }
}
