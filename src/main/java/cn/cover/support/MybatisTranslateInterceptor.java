package cn.cover.support;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MybatisTranslateInterceptor implements Interceptor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final SqlTranslator sqlTranslator;
    
    public MybatisTranslateInterceptor(SqlTranslator sqlTranslator) {
        this.sqlTranslator = sqlTranslator;
    }
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] invocationArgs = invocation.getArgs();
        MappedStatement statement = (MappedStatement) invocationArgs[0];
        Object parameter = invocationArgs[1];
    
        BoundSql originBoundSql;
        if (invocationArgs.length > 4) {
            originBoundSql = (BoundSql) invocationArgs[5];
        } else {
            originBoundSql = statement.getBoundSql(parameter);
        }
        String translateSql = sqlTranslator.translate(originBoundSql.getSql(), statement.getId());
        if (logger.isDebugEnabled()) {
            logger.debug("原始SQL:{} 转译后SQL:{}", originBoundSql.getSql(), translateSql);
        }
        
        if (invocationArgs.length > 4) {
            // 对应6个参数的query方法
            BoundSql newBoundSql = newBoundSql(statement, originBoundSql, translateSql);
            invocationArgs[5] = newBoundSql;
        } else {
            MappedStatement newMappedStatement = newMappedStatement(statement, originBoundSql, translateSql);
            invocationArgs[0] = newMappedStatement;
        }
        return invocation.proceed();
    }
    
    /**
     * 创建新的SqlSource
     *
     * @param statement    源ms
     * @param originBoundSql     源boundSql
     * @param translateSql 翻译之后的sql
     */
    private BoundSql newBoundSql(MappedStatement statement, BoundSql originBoundSql, String translateSql) {
        return new BoundSql(statement.getConfiguration(), translateSql, originBoundSql.getParameterMappings(), originBoundSql.getParameterObject());
    }
    
    /**
     * 克隆MappedStatement
     *
     * @param statement    源ms
     * @param boundSql     源boundSql
     * @param translateSql 翻译之后的sql
     */
    private MappedStatement newMappedStatement(MappedStatement statement, BoundSql boundSql, String translateSql) {
        SqlSource staticSqlSource = new StaticSqlSource(statement.getConfiguration(), translateSql, boundSql.getParameterMappings());
        
        // 复制属性
        MappedStatement.Builder builder = new MappedStatement.Builder(statement.getConfiguration(), statement.getId(), staticSqlSource, statement.getSqlCommandType());
        builder.resource(statement.getResource());
        builder.parameterMap(statement.getParameterMap());
        builder.resultMaps(statement.getResultMaps());
        builder.fetchSize(statement.getFetchSize());
        builder.timeout(statement.getTimeout());
        builder.statementType(statement.getStatementType());
        builder.resultSetType(statement.getResultSetType());
        builder.cache(statement.getCache());
        builder.flushCacheRequired(statement.isFlushCacheRequired());
        builder.useCache(statement.isUseCache());
        builder.resultOrdered(statement.isResultOrdered());
        builder.keyGenerator(statement.getKeyGenerator());
        builder.keyProperty(ArraysToString(statement.getKeyProperties()));
        builder.keyColumn(ArraysToString(statement.getKeyColumns()));
        builder.databaseId(statement.getDatabaseId());
        builder.lang(statement.getLang());
        builder.resulSets(ArraysToString(statement.getResulSets()));
        
        // 创建新的 MappedStatement 对象
        return builder.build();
    }
    
    private String ArraysToString(String[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(",").append(array[i]);
        }
        return sb.toString();
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
    
    }
}
