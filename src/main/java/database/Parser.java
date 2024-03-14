package database;

import database.sql.SQL;
import exception.SqlTranslateException;

/**
 * SQL解析器
 */
public interface Parser {
    
    /**
     * 解析SQL
     * @param originSQL
     * @return null：解析失败 否则解析后的SQL语法树
     */
    SQL parse(String originSQL) throws SqlTranslateException;
}
