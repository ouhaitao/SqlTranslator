package cn.cover.database.parser.mysql.visitor.dm.support;

import static cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil.SQL_DS;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/29 17:59
 */
public enum SqlEnum {
  SELECT,
  FROM,
  INSERT,
  VALUES,
  INTO,
  UPDATE,
  SET,
  DELETE,
  JOIN,
  LEFT_JOIN,
  RIGHT_JOIN,
  INNER_JOIN,
  FULL_JOIN,
  UNION,
  UNION_ALL
  ;

  public String getName() {
    return this.name().replace("_", SQL_DS);
  }

  public void append(StringBuilder sqlBuilder) {
    if (sqlBuilder != null) {
      sqlBuilder.append(this.getName()).append(SQL_DS);
    }
  }
}
