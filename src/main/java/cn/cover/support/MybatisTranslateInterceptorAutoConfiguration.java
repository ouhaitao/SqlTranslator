package cn.cover.support;

import cn.cover.util.CollectionUtils;
import com.github.pagehelper.dialect.helper.MySqlDialect;
import com.github.pagehelper.page.PageAutoDialect;
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
        initPageHelperDialect();
        return new MybatisTranslateInterceptor(sqlTranslator);
    }
    
    /**
     * PageHelper的自动方言对象
     * pageHelper如果没有配置方言，则会根据jdbc的url获取到对应的方言对象，这里达梦使用Mysql的方言
     */
    private void initPageHelperDialect() {
        try {
            Class.forName("com.github.pagehelper.page.PageAutoDialect");
            PageAutoDialect.registerDialectAlias("dm", MySqlDialect.class);
        } catch (ClassNotFoundException e) {
            logger.info("未找到com.github.pagehelper.page.PageAutoDialect,不初始化PageHelper方言");
        }
    }
}
