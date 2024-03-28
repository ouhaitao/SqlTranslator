package cn.cover.database.parser.mysql;

import cn.cover.exception.SqlTranslateException;
import org.junit.Test;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 14:35
 */
public class DMSqlParserTest {

  private static final DMSqlParser DM_SQL_PARSER = new DMSqlParser();

  public static void main(String[] args) throws SqlTranslateException {
    //String sql =
    //    "SELECT ifnull(tab1.id, 0),group_concat(tab1.name) name,`tab3.org` FROM tab1 left join tab3 on tab1.id = tab3.tt_id where "
    //        //+ "tab1.id in (select t_id from tab2)"
    //        + " tab1.name = \"haha\""
    //        + " and tab1.id = 10"
    //        + ""
    //        + " limit 1,1";

    //String sql = "INSERT INTO `fm_auth_menu`(a,b,c,d,e,f,g) VALUES (92, 'AUTH', '权限管理', 0, 1, 3, NULL);";
    //String sql = "select  *  from `user` where user_name = ?";
    //String sql = "update USER set a=12,b=12,c='haha' where id = 13 and b = ?";
    String sql = "delete from USER where id = 13 and b = ?";
    System.out.println(DM_SQL_PARSER.parse(sql));
  }

  @Test
  public void testFromSubSelect() throws SqlTranslateException {
    String sql = "SELECT\n"
        + "\t* \n"
        + "FROM\n"
        + "\t( SELECT * FROM config WHERE project_id = ? ) a \n"
        + "WHERE\n"
        + "\ta.NAME = 'haha'";
    System.out.println(DM_SQL_PARSER.parse(sql));
  }

  @Test
  public void testSubSelect() throws SqlTranslateException {
    String sql = "select * from a where a.id in (select a_id from c)";
    System.out.println(DM_SQL_PARSER.parse(sql));
  }

  @Test
  public void testLimit() throws SqlTranslateException {
    String sql = "select * from a limit 1";
    System.out.println(DM_SQL_PARSER.parse(sql));
  }


  @Test
  public void projectTest() throws SqlTranslateException {
    String sql = "SELECT\n"
        + "\tcount( 0 ) \n"
        + "FROM\n"
        + "\t(\n"
        + "\tSELECT\n"
        + "\t\tp.id AS project_id,\n"
        + "\t\tp.project_name,\n"
        + "\t\tc.id AS config_id,\n"
        + "\t\tc.config_key,\n"
        + "\t\tc.config_value,\n"
        + "\t\tc.config_desc,\n"
        + "\t\t0 AS config_type \n"
        + "\tFROM\n"
        + "\t\tconfig c\n"
        + "\t\tINNER JOIN project p ON c.project_id = p.id \n"
        + "\tWHERE\n"
        + "\t\tp.team_id = ? UNION\n"
        + "\tSELECT\n"
        + "\t\tt.id AS project_id,\n"
        + "\t\tCONCAT( '团队配置[', t.team_name, ']' ) AS project_id,\n"
        + "\t\tc.id AS config_id,\n"
        + "\t\tc.config_key,\n"
        + "\t\tc.config_value,\n"
        + "\t\tc.config_desc,\n"
        + "\t\t1 AS config_type \n"
        + "\tFROM\n"
        + "\t\tconfig c\n"
        + "\t\tINNER JOIN team t ON c.team_id = t.id \n"
        + "WHERE\n"
        + "\tt.id = ?) table_count";


    System.out.println(DM_SQL_PARSER.parse(sql));
  }




}
