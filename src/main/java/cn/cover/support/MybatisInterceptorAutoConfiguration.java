package cn.cover.support;

import cn.cover.util.CollectionUtils;
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
@ConditionalOnProperty(prefix = "sql.translator", value = {"originDatabase", "targetDatabase"}, matchIfMissing = true)
public class MybatisInterceptorAutoConfiguration {

    public static final String MYBATIS_INTERCEPTOR_BEAN_NAME = "sqlTranslatorMybatisInterceptor";

    @Bean(MYBATIS_INTERCEPTOR_BEAN_NAME)
    public MybatisInterceptor mybatisInterceptor(SqlTranslatorProperties properties) {
        SqlTranslator.Builder builder = SqlTranslator.builder()
            .originDatabase(properties.getOriginDatabase())
            .targetDatabase(properties.getTargetDatabase());
        if (!CollectionUtils.isEmpty(properties.getIgnoreMapperIdSet())) {
            properties.getIgnoreMapperIdSet().forEach(builder::addIgnoreMapperId);
        }
        SqlTranslator sqlTranslator = builder.build();
        return new MybatisInterceptor(sqlTranslator);
    }
}
