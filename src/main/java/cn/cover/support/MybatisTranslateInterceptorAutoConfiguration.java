package cn.cover.support;

import cn.cover.util.CollectionUtils;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.dialect.helper.MySqlDialect;
import com.github.pagehelper.page.PageAutoDialect;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author parry 2024/03/26
 * mybatis拦截器自动配置
 */
@Configuration
@EnableConfigurationProperties(SqlTranslatorProperties.class)
public class MybatisTranslateInterceptorAutoConfiguration {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String MYBATIS_INTERCEPTOR_BEAN_NAME = "sqlTranslatorMybatisInterceptor";
    
    /**
     * PageHelper的拦截器是在afterPropertiesSet方法中添加
     * 所以MybatisTranslateInterceptor会先于PageHelper的拦截器被添加进去，所以会在PageHelper后面执行
     */
    @Bean(MYBATIS_INTERCEPTOR_BEAN_NAME)
    @ConditionalOnProperty(prefix = "sql.translator", value = {"origin-database", "target-database"})
//    @ConditionalOnBean(SqlSessionFactory.class)
    public MybatisTranslateInterceptor mybatisInterceptor(SqlTranslatorProperties properties, List<SqlSessionFactory> sqlSessionFactoryList) {
        checkPageHelperInterceptor(sqlSessionFactoryList);
        logger.info("创建MybatisTranslateInterceptor, properties={}", properties.toString());
        SqlTranslator.Builder builder = SqlTranslator.builder()
            .originDatabase(properties.getOriginDatabase())
            .targetDatabase(properties.getTargetDatabase());
        if (!CollectionUtils.isEmpty(properties.getIgnoreMapperIdSet())) {
            properties.getIgnoreMapperIdSet().forEach(builder::addIgnoreMapperId);
        }
        SqlTranslator sqlTranslator = builder.build();
        initPageHelperDialect();
        MybatisTranslateInterceptor mybatisTranslateInterceptor = new MybatisTranslateInterceptor(sqlTranslator);
        sqlSessionFactoryList.stream()
            .map(SqlSessionFactory::getConfiguration)
            .filter(configuration -> {
                List<Interceptor> interceptorList = configuration.getInterceptors();
                if (CollectionUtils.isEmpty(interceptorList)) {
                    return true;
                }
                return interceptorList.stream()
                    .anyMatch(interceptor -> !MybatisTranslateInterceptor.class.isAssignableFrom(interceptor.getClass()));
            })
            .forEach(configuration -> {
                configuration.addInterceptor(mybatisTranslateInterceptor);
                logger.info("向sqlSessionFactory添加MybatisTranslateInterceptor成功");
            });
        return mybatisTranslateInterceptor;
    }
    
    /**
     * PageHelper的自动方言对象
     * pageHelper如果没有配置方言，则会根据jdbc的url获取到对应的方言对象
     * 由于项目中使用的mysql的语法，所以统一使用Mysql的方言
     */
    private void initPageHelperDialect() {
        try {
            Class.forName("com.github.pagehelper.page.PageAutoDialect");
            PageAutoDialect.registerDialectAlias("dm", MySqlDialect.class);
        } catch (ClassNotFoundException e) {
            logger.info("未找到com.github.pagehelper.page.PageAutoDialect,不初始化PageHelper方言");
        }
    }
    
    /**
     * 校验sqlSessionFactory中是否已经设置的pageInterceptor
     */
    private void checkPageHelperInterceptor(List<SqlSessionFactory> sqlSessionFactoryList) {
        boolean match;
        try {
            Class.forName("com.github.pagehelper.PageInterceptor");
            match = sqlSessionFactoryList.stream()
                .map(SqlSessionFactory::getConfiguration)
                .flatMap(configuration -> configuration.getInterceptors().stream())
                .anyMatch(interceptor -> interceptor.getClass().isAssignableFrom(PageInterceptor.class));
        } catch (ClassNotFoundException e) {
            return;
        }
        if (match) {
            throw new RuntimeException("检测到sqlSession中已添加PageInterceptor,需要修改顺序");
        }
    }
}
