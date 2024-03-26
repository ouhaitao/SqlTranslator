package cn.cover.database;

import cn.cover.database.sql.select.Select;

/**
 * SQL转译器
 */
public interface Translator {
    
    /**
     * 转译select
     */
    String select(Select sql);
}
