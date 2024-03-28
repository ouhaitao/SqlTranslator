package cn.cover.database.parser.mysql.visitor.dm;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/28 9:14
 */
public interface Context<T> {

  T getContext();

  void setContext(T c);
}
