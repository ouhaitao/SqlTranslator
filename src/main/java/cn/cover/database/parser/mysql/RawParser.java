package cn.cover.database.parser.mysql;

import cn.cover.database.sql.SQL;
import cn.cover.database.Parser;
import cn.cover.database.sql.RawSQL;
import cn.cover.exception.SqlTranslateException;

/**
 * @author parry 2024/03/26
 */
public class RawParser implements Parser {
    
    @Override
    public SQL parse(String originSQL) throws SqlTranslateException {
        return new RawSQL(originSQL);
    }
}
