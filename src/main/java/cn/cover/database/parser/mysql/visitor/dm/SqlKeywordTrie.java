package cn.cover.database.parser.mysql.visitor.dm;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Use https://blog.csdn.net/qq_37358909/article/details/108623240
 * @Author: jeff
 * @Date: 2024/3/27 14:14
 */
public class SqlKeywordTrie {

  private static final Set<String> KEYWORD_LIST = Arrays.stream(Keyword.values())
      .map(Keyword::getName).collect(
          Collectors.toSet());

  public static boolean contains(String keyword) {
    return KEYWORD_LIST.contains(keyword);
  }

  enum Keyword {
    // A
    ABORT("ABORT", KeyType.COMMON),
    ABSOLUTE("ABSOLUTE", KeyType.RESERVED),
    ABSTRACT("ABSTRACT", KeyType.RESERVED),
    ACCESSED("ACCESSED", KeyType.COMMON),
    ACCOUNT("ACCOUNT", KeyType.COMMON),
    ACROSS("ACROSS", KeyType.COMMON),
    ACTION("ACTION", KeyType.COMMON),
    ADD("ADD", KeyType.RESERVED),
    ADMIN("ADMIN", KeyType.RESERVED),

    // D
    DOMAIN("DOMAIN", KeyType.RESERVED),

    // U
    USER("USER", KeyType.RESERVED),


    // T
    TYPE("TYPE", KeyType.COMMON),

    ;
    private String name;
    private KeyType type;

    Keyword(final String name, final KeyType type) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public KeyType getType() {
      return type;
    }

    public void setType(final KeyType type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return "Keyword{" +
          "name='" + name + '\'' +
          ", type=" + type +
          '}';
    }
  }

  enum KeyType {
    COMMON, RESERVED
  }
}
