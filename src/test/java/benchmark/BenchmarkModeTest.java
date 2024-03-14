package benchmark;

import database.parser.mysql.MySQLParser;
import database.sql.SQL;
import database.translator.MySQLTranslator;
import exception.SqlTranslateException;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author parry 2024/01/29
 */
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 10, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, jvmArgsAppend = "-XX:+PrintCompilation")
@Threads(10)
public class BenchmarkModeTest {
    
    
    @Benchmark
    public void testMethod() throws SqlTranslateException {
        MySQLParser parser = new MySQLParser();
        SQL parse = parser.parse("SELECT\n" +
            "\tsource.id,\n" +
            "\tsource.name as sourceName,\n" +
            "\tdate_sub(now(), interval 1 hour),\n" +
            "\tconcat('concatString', source.name),\n" +
            "\t(select id from a where id = 1) as aId\n" +
            "FROM\n" +
            "\tfm_common_source source\n" +
            "\tleft join a tableA on source.id = tableA.id and (source.id = tableA.id)\n" +
            "WHERE\n" +
            "\t(source.id >= 1\n" +
            "\tAND source.id <= 10)\n" +
            "\tand exists(select 1)" +
            "\tand exists(exists(exists(select 1)))\n" +
            "GROUP BY\n" +
            "\tsource.id\n" +
            "order by\n" +
            "\tsource.id,\n" +
            "\ttableA.id desc\n" +
            "limit 5");
        new MySQLTranslator().translate(parse);
    }
    
}
