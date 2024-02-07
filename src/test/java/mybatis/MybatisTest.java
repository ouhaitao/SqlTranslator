package mybatis;


import com.mysql.cj.jdbc.MysqlDataSource;
import mybatis.mapper.TestMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;
import support.MybatisInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * @author parry 2024/02/07
 */
public class MybatisTest {
    
    private TestMapper testMapper;
    
    @Before
    public void mapper() throws IOException {
//        创建数据源
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUser("root");
        dataSource.setPassword("iamalone11");
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addInterceptor(new MybatisInterceptor());
        configuration.addMapper(TestMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        testMapper = sqlSessionFactory.openSession(true).getMapper(TestMapper.class);
    }
    
    @Test
    public void testInterceptor() {
        Map<Object, Object> select = testMapper.select(1);
        System.out.println(select.get("id"));
    }
}
