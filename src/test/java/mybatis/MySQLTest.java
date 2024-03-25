package mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import mybatis.mapper.TestMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.SqlTranslatorUtil;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author parry 2024/02/07
 */
public class MySQLTest {
    
    private TestMapper testMapper;
    
    private static DataSource dataSource;
    
    @BeforeClass
    public static void dataSource() {
        String url = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
        String user = "root";
        String password = "iamalone11";
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(user);
        druidDataSource.setPassword(password);
        dataSource = druidDataSource;
    }
    
    @Before
    public void mapper() {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addInterceptor(SqlTranslatorUtil.getMybatisInterceptor());
        configuration.addMapper(TestMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        testMapper = sqlSessionFactory.openSession(true).getMapper(TestMapper.class);
    }
    
    @Test
    public void testInterceptor() {
        Map<Object, Object> select = testMapper.mysqlSelect(1);
        System.out.println(select.get("id"));
    }
}
