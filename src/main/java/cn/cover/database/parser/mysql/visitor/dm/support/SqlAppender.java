package cn.cover.database.parser.mysql.visitor.dm.support;

import java.util.Objects;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/4/1 10:26
 */
public class SqlAppender {

  private final StringBuilder stringBuilder;

  public SqlAppender() {
    this.stringBuilder = new StringBuilder();
  }

  public SqlAppender append(Object obj) {
    if (Objects.nonNull(obj)) {
      stringBuilder.append(obj).append(SqlUtil.WHITE_SPACE);
    }
    return this;
  }

  public SqlAppender appendClose(Object obj) {
    if (Objects.nonNull(obj)) {
      stringBuilder.append(obj);
    }
    return this;
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}
