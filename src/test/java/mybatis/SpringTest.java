package mybatis;

import cn.cover.database.sql.Database;
import cn.cover.support.MybatisTranslateInterceptor;
import cn.cover.support.MybatisTranslateInterceptorAutoConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Set;

/**
 * @author parry 2024/03/26
 */
@SpringBootTest
public class SpringTest {
    
    private ApplicationContextRunner runner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MybatisTranslateInterceptorAutoConfiguration.class));
    
    /**
     * 源数据库类型
     */
    private String originDatabase = "sql.translator.origin-database=" + Database.RAW;
    /**
     * 目标数据库类型
     */
    private String targetDatabase = "sql.translator.target-database=" + Database.RAW;
    
    private Set<String> ignoreMapperIdSet;
    
    @Test
    public void testAutoConfiguration() {
        runner.withPropertyValues(originDatabase, targetDatabase)
            .run(context -> {
                MybatisTranslateInterceptor interceptor = context.getBean(MybatisTranslateInterceptor.class);
                System.out.println(interceptor);
            });
    }
}
