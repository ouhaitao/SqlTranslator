package mybatis;

import cn.cover.database.sql.Database;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author parry 2024/03/25
 */
public class DmTest {
    
    private TestMapper testMapper;
    
    private static DataSource dataSource;
    
    @BeforeClass
    public static void dataSource() {
        String url = "jdbc:dm://127.0.0.1:5236/test_model";
        String user = "SYSDBA";
        String password = "SYSDBA";
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
        configuration.addMapper(TestMapper.class);
        configuration.addInterceptor(SqlTranslatorUtil.getMybatisInterceptor(Database.DM, Database.RAW));
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        testMapper = sqlSessionFactory.openSession(true).getMapper(TestMapper.class);
    }
    
    @Test
    public void test() {
        Map<Object, Object> select = testMapper.dmSelect(1);
        select.forEach((key, value) -> System.out.println(key + ":" + value));
        select = testMapper.dmSelect(2);
    }
    
    @Test
    public void testForeach() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        Map<Object, Object> select = testMapper.dmSelectForeach(list);
        select.forEach((key, value) -> System.out.println(key + ":" + value));
    }
    
    @Test
    public void testResultMapWithCollection() {
        Map<Object, Object> select = testMapper.dmSelectCollection(1);
        select.forEach((key, value) -> System.out.println(key + ":" + value));
    }
}
