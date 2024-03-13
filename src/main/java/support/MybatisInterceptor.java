package support;

import database.parser.mysql.MySQLParser;
import database.sql.SQL;
import database.translator.MySQLTranslator;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author parry 2024/02/07
 */
@Intercepts(value = {
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class})
})
public class MybatisInterceptor implements Interceptor {
    
    private Field sqlSourceField;
    
    private SqlTranslator sqlTranslator;
    
    public MybatisInterceptor(SqlTranslator sqlTranslator) {
        try {
            this.sqlTranslator = sqlTranslator;
            sqlSourceField = MappedStatement.class.getDeclaredField("sqlSource");
            sqlSourceField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = statement.getBoundSql(parameter);
        String translateSql = sqlTranslator.translate(boundSql.getSql(), statement.getId());
        updateSql(statement, boundSql, translateSql);
        
        return invocation.proceed();
    }
    
    /**
     * 通过反射修改statement的SqlSource
     */
    private void updateSql(MappedStatement statement, BoundSql boundSql, String translateSql) throws Throwable {
        // 创建新的静态SqlSource
        StaticSqlSource staticSqlSource = new StaticSqlSource(statement.getConfiguration(), translateSql, boundSql.getParameterMappings());
        
        // 反射修改sqlSource
        sqlSourceField.set(statement, staticSqlSource);
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
    
    }
}
