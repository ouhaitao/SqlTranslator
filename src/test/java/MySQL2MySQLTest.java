import database.parser.mysql.MySQLParser;
import database.sql.SQL;
import database.translator.MySQLTranslator;
import exception.SqlTranslateException;
import org.junit.Test;

/**
 * @author parry 2024/01/22
 */
public class MySQL2MySQLTest {

    private final String selectSql = "SELECT distinct \n" +
        "\tsource.*,\n" +
        "\t`source.id`,\n" +
        "\tsource.name as sourceName,\n" +
        "\tdate_sub(now(), interval 1 hour),\n" +
        "\tconcat('concatString', source.name),\n" +
        "\tcount(distinct source.name),\n" +
        "\t(select id from a where id = 1) as aId,\n" +
        "\tdate_sub((now()), interval 1 hour),\n" +
        "\tcase when source.id = 1 then 1 else source.id end,\n" +
        "\tcase when (source.id = 1 or source.id = 2) then 1 else source.id end,\n" +
        "\tcase when (source.id = 1) or (source.id = 2) then 1 end,\n" +
        "\t(case source.id when 1 then 1 else source.id end) as caseTest\n" +
        "FROM\n" +
        "\tfm_common_source source\n" +
        "\tleft join a tableA on source.id = tableA.id and (source.id = tableA.id)\n" +
        "\tleft join (select * from a) tableSubSelect on source.id = tableSubSelect.id\n" +
        "WHERE\n" +
        "\t(source.id >= 1\n" +
        "\tAND source.id <= 10)\n" +
        "\tand exists(select 1)" +
        "\tand exists(exists(not exists(select 1)))\n" +
        "\tand source.id is null\n" +
        "\tand source.id is not null\n" +
        "\tand source.type in (select id from typeTable)\n" +
        "GROUP BY\n" +
        "\tsource.id\n" +
        "\thaving count(*) > 1 and count(*) < 3\n" +
        "order by\n" +
        "\tsource.id,\n" +
        "\ttableA.id desc\n" +
        "limit 5\n";
    
    private final String updateSql = "update table1 a left join table2 b on a.id = b.id\n" +
        "set\n" +
        "a.column1 = '1',\n" +
        "a.`column2` = '2',\n" +
        "a.column3 = b.column3,\n" +
        "a.column4 = (select id from table3 where id = a.id),\n" +
        "a.column5 = a.column5 + 1,\n" +
        "a.column6 = (a.column6 - 1)\n" +
        "where a.id = 1 and a.type in (select id from typeTable where id = 2)";
    
    @Test
    public void selectTest() throws SqlTranslateException {
//        String sql = this.sql;
//        System.out.println(sql);
        MySQLParser parser = new MySQLParser();
        SQL parse = parser.parse(selectSql);
        MySQLTranslator translator = new MySQLTranslator();
        String translate = translator.translate(parse);
        System.out.println(translate);
    }
    
    @Test
    public void updateTest() throws SqlTranslateException {
        MySQLParser parser = new MySQLParser();
        SQL parse = parser.parse(updateSql);
//        MySQLTranslator translator = new MySQLTranslator();
//        String translate = translator.translate(parse);
//        System.out.println(translate);
    }

}
