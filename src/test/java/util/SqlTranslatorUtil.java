package util;

import cn.cover.database.sql.Database;
import cn.cover.support.MybatisTranslateInterceptor;
import cn.cover.support.SqlTranslator;

/**
 * @author parry 2024/03/13
 */
public class SqlTranslatorUtil {
    
    public static SqlTranslator getSqlTranslator() {
        return getSqlTranslator(Database.MYSQL, Database.MYSQL);
    }
    
    public static SqlTranslator getSqlTranslator(Database originDatabase, Database targetDatabase) {
        return SqlTranslator.builder().originDatabase(originDatabase).targetDatabase(targetDatabase).build();
    }
    
    public static MybatisTranslateInterceptor getMybatisInterceptor() {
        return new MybatisTranslateInterceptor(getSqlTranslator());
    }
    
    public static MybatisTranslateInterceptor getMybatisInterceptor(Database originDatabase, Database targetDatabase) {
        return new MybatisTranslateInterceptor(getSqlTranslator(originDatabase, targetDatabase));
    }
}
