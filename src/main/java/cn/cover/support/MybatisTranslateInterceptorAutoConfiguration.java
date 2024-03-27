package cn.cover.support;

import cn.cover.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author parry 2024/03/26
 * mybatis拦截器自动配置
 */
@Configuration
@EnableConfigurationProperties(SqlTranslatorProperties.class)
@ConditionalOnProperty(prefix = "sql.translator", value = {"origin-database", "target-database"})
public class MybatisTranslateInterceptorAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String MYBATIS_INTERCEPTOR_BEAN_NAME = "sqlTranslatorMybatisInterceptor";

    @Bean(MYBATIS_INTERCEPTOR_BEAN_NAME)
    public MybatisTranslateInterceptor mybatisInterceptor(SqlTranslatorProperties properties) {
        logger.info("创建MybatisTranslateInterceptor, properties={}", properties.toString());
        SqlTranslator.Builder builder = SqlTranslator.builder()
            .originDatabase(properties.getOriginDatabase())
            .targetDatabase(properties.getTargetDatabase());
        if (!CollectionUtils.isEmpty(properties.getIgnoreMapperIdSet())) {
            properties.getIgnoreMapperIdSet().forEach(builder::addIgnoreMapperId);
        }
        SqlTranslator sqlTranslator = builder.build();
        return new MybatisTranslateInterceptor(sqlTranslator);
    }
}
