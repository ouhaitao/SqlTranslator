package database.parser.mysql;

import database.Parser;
import database.sql.RawSQL;
import database.sql.SQL;
import exception.SqlTranslateException;

/**
 * @author parry 2024/03/26
 */
public class RawParser implements Parser {
    
    @Override
    public SQL parse(String originSQL) throws SqlTranslateException {
        return new RawSQL(originSQL);
    }
}
