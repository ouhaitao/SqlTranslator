package database;

import database.sql.SQL;

/**
 * SQL解析器
 */
public interface Parser {
    
    SQL parse(String originSQL);
}
