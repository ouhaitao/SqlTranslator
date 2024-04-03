package cn.cover.database.parser.mysql.visitor.dm.support;

import static cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil.WHITE_SPACE;

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
  LEFT_JOIN("LEFT JOIN"),
  RIGHT_JOIN("RIGHT JOIN"),
  INNER_JOIN("INNER JOIN"),
  FULL_JOIN("FULL JOIN"),
  UNION,
  UNION_ALL("UNION ALL"),
  ON,
  WHERE,
  AS,
  LIMIT,
  NOT,
  LIKE,
  IS,
  NULL,
  AND,
  IN,
  OR,


  COMMA(SqlUtil.COMMA),
  QUOTE(SqlUtil.QUOTE, false),
  DOUBLE_QUOTE(SqlUtil.DOUBLE_QUOTE, false),
  DOT(SqlUtil.DOT, false),
  Equals("="),
  NOT_EQUALS("!="),
  LEFT_PARENTHESIS("("),
  RIGHT_PARENTHESIS(" )"),
  QUESTION_MARK("?"),
  ASTERISK("*"),
  ;

  private final String name;
  /**
   * 需要在关键字后面加空格
   */
  private final boolean needWS;

  SqlEnum() {
    name = this.name();
    needWS = true;
  }

  SqlEnum(final String name) {
    this.name = name;
    needWS = true;
  }

  SqlEnum(final String name, final boolean needWS) {
    this.name = name;
    this.needWS = needWS;
  }

  public void append(StringBuilder sqlBuilder) {
    if (sqlBuilder == null) {
      return;
    }
    if (needWS) {
      sqlBuilder.append(this.name).append(WHITE_SPACE);
      return;
    }
    sqlBuilder.append(this.name);
  }

  public void append(SqlAppender sqlAppender) {
    if (sqlAppender == null) {
      return;
    }
    if (needWS) {
      sqlAppender.append(this.name);
      return;
    }
    sqlAppender.appendClose(this.name);
  }

  //public void append(SqlAppender sqlAppender, boolean ws) {
  //  if (sqlAppender == null) {
  //    return;
  //  }
  //  if (ws) {
  //    sqlAppender.append(this.name);
  //    return;
  //  }
  //  sqlAppender.appendClose(this.name);
  //}
}
