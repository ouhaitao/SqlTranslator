package database;

import database.sql.select.Select;

/**
 * SQL转译器
 */
public interface Translator {
    
    /**
     * 转译select
     */
    String select(Select sql);
}
