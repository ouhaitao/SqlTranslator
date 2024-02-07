package database;

import database.sql.column.DatabaseFunction;
import database.sql.column.Function;

public interface FunctionTranslator {
    
    Function translate(DatabaseFunction source);
    
    boolean support(DatabaseFunction databaseFunction);
}
