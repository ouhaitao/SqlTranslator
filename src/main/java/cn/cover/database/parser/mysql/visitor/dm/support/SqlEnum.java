package cn.cover.database.parser.mysql.visitor.dm.support;

import static cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil.SQL_DS;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/29 17:59
 */
public enum SqlEnum {
  SELECT,

  ;

  public void append(StringBuilder sqlBuilder) {
    if (sqlBuilder != null) {
      sqlBuilder.append(this.name()).append(SQL_DS);
    }
  }
}
