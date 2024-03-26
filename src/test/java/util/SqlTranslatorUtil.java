package util;

import cn.cover.database.sql.Database;
import cn.cover.support.MybatisInterceptor;
import cn.cover.support.SqlTranslator;

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
