package cn.cover.database.parser.mysql.visitor.dm;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 09:40
 */
public class ContextAttr {
  //HashBiMap

  private final Multimap<String, String> tableAliasMap = LinkedListMultimap.create();

  private String originSql;

  private StringBuilder sqlBuilder;

  public boolean putTable(String table, String alias) {
    return tableAliasMap.put(table, alias);
  }

  public Collection<String> getTableAlias(String table) {
    return tableAliasMap.get(table);
  }

  public String getOriginSql() {
    return originSql;
  }

  public void setOriginSql(final String originSql) {
    this.originSql = originSql;
  }

  public StringBuilder getSqlBuilder() {
    return sqlBuilder;
  }

  public void setSqlBuilder(final StringBuilder sqlBuilder) {
    this.sqlBuilder = sqlBuilder;
  }
}
