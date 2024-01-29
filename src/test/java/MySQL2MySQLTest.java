import database.parser.mysql.MySQLParser;
import database.sql.SQL;
import database.translator.MySQLTranslator;
import org.junit.Test;

/**
 * @author parry 2024/01/22
 */
public class MySQL2MySQLTest {

    private final String sql = "SELECT\n" +
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
        "limit 5";
    
    private final String count = "SELECT\n" +
        "\tcount(*)\n" +
        "FROM\n" +
        "\tfm_common_source source\n" +
        "\tleft join a tableA on source.id = tableA.id\n" +
        "WHERE\n" +
        "\tsource.id >= 1\n" +
        "\tAND source.id <= 10\n" +
        "\tand exists(select 1)\n";
    
    @Test
    public void parseTest() {
//        String sql = this.sql;
//        System.out.println(sql);
        MySQLParser parser = new MySQLParser();
        SQL parse = parser.parse(sql);
        MySQLTranslator translator = new MySQLTranslator();
        System.out.println(translator.translate(parse));
    }

}
