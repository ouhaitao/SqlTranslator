package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 9:14
 */
public interface Context {

  ContextAttr getContext();

  void setContext(ContextAttr c);

  boolean putTable(String table, String alias);

  SqlAppender sqlBuild();
}
