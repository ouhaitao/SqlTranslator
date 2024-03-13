package util;

import database.sql.Database;
import support.MybatisInterceptor;
import support.SqlTranslator;

/**
 * @author parry 2024/03/13
 */
public class SqlTranslatorUtil {
    
    public static SqlTranslator getSqlTranslator() {
        return SqlTranslator.builder().originDatabase(Database.MYSQL).targetDatabase(Database.MYSQL).build();
    }
    
    public static MybatisInterceptor getMybatisInterceptor() {
        return new MybatisInterceptor(getSqlTranslator());
    }
}
