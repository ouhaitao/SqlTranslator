package cn.cover.database.parser.mysql.visitor.dm;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/27 14:45
 */
public class CommonVisitor {

  public static String dealKeyword(String origin) {
    final boolean contains = SqlKeywordTrie.contains(origin);
    if (contains) {
      return "\"" + origin + "\"";
    }
    return origin;
  }
}