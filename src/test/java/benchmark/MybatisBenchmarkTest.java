package benchmark;

import com.mysql.cj.jdbc.MysqlDataSource;
import mybatis.mapper.TestMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import util.SqlTranslatorUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author parry 2024/02/08
 */
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 10, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(10)
@State(Scope.Benchmark)
@Fork(1)
public class MybatisBenchmarkTest {
    
    /**
     * 不带interceptor
     */
    private TestMapper mapper;
    /**
     * 带interceptor
     */
    private TestMapper mapperWithInterceptor;
    
    private Environment environment;
    
    @Setup
    public void setEnvironment() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUser("root");
        dataSource.setPassword("iamalone11");
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        environment = new Environment("development", transactionFactory, dataSource);
    }
    
    @Setup
    public void setMapper() {
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TestMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        mapper = sqlSessionFactory.openSession(true).getMapper(TestMapper.class);
    }
    
    @Setup
    public void setMapperWithInterceptor() {
        Configuration configuration = new Configuration(environment);
        configuration.addInterceptor(SqlTranslatorUtil.getMybatisInterceptor());
        configuration.addMapper(TestMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        mapperWithInterceptor = sqlSessionFactory.openSession(true).getMapper(TestMapper.class);
    }
    
    @Benchmark
    public void noInterceptor() {
        Map<Object, Object> select = mapper.mysqlSelect(1);
    }
    
    @Benchmark
    public void haveInterceptor() {
        Map<Object, Object> select = mapperWithInterceptor.mysqlSelect(1);
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
            .include(MybatisBenchmarkTest.class.getSimpleName())
            .resultFormat(ResultFormatType.JSON)
            .result("src/test/resources/MybatisBenchmarkTest.json")
            .build();
        new Runner(options).run();
    }
}
