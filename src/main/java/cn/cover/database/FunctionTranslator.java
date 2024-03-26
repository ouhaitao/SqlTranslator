package cn.cover.database;

import cn.cover.database.sql.column.DatabaseFunction;
import cn.cover.database.sql.column.Function;

public interface FunctionTranslator {
    
    Function translate(DatabaseFunction source);
    
    boolean support(DatabaseFunction databaseFunction);
}
