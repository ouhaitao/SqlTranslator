package cn.cover.database.parser.mysql.visitor.dm.support;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/29 18:01
 */
public class SqlUtil {

  // sql分隔符
  public static final String SQL_DS = " ";

  public static StringBuilder delimiter(StringBuilder sqlBuild) {
    if (sqlBuild != null) {
      sqlBuild.append(SQL_DS);
    }
    return sqlBuild;
  }

  public static StringBuilder appendSqlEnum(StringBuilder sqlBuild, SqlEnum sqlEnum) {
    if (sqlBuild != null) {
      sqlBuild.append(sqlEnum.name()).append(SQL_DS);
    }
    return sqlBuild;
  }
}
