package cn.cover.support;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;


/**
 * @author parry 2024/04/02
 * 用于处理
 */
@Configuration
@ConditionalOnProperty(prefix = "sql.translator", value = {"ignore-collection-init-sqls"})
@EnableConfigurationProperties(SqlTranslatorProperties.class)
public class IgnoreCollectionInitSqlsAutoconfiguration {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SqlTranslatorProperties properties;
    
    public IgnoreCollectionInitSqlsAutoconfiguration() {
        logger.info("检测到sql.translator.ignore-collection-init-sqls配置={}", properties.getIgnoreCollectionInitSqls());
    }
    
    @Bean
    @ConditionalOnBean(name = "com.alibaba.druid.pool.DruidDataSource")
    public DruidDataSourceBeanPostProcessor druidDataSourceIgnoreAutoconfiguration() {
        return new DruidDataSourceBeanPostProcessor();
    }
    
    public class DruidDataSourceBeanPostProcessor implements BeanPostProcessor {
    
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (DruidDataSource.class.isAssignableFrom(bean.getClass())) {
                Set<String> ignoreCollectionInitSqls = properties.getIgnoreCollectionInitSqls();
                ((DruidDataSource) bean).getConnectionInitSqls().removeIf(initSql -> {
                    logger.info("找到DruidDataSource,删除connectionInitSqls={}", initSql);
                    return ignoreCollectionInitSqls.contains(initSql);
                });
            }
            return bean;
        }
    }
    
}
