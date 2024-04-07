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
    //String sql = "delete from USER where id = 13 and b = ?";

    //String sql = "SELECT ur.role_id roleId, ur.user_id userId FROM fm_user_userrole ur WHERE ur.user_id=?";


    String sql = "select draft_article_id , count(1) num\n"
        + "       from fm_common_image\n"
        + "       where draft_article_id in\n"
        + "        (  \n"
        + "           ?\n"
        + "          ) \n"
        + "       and is_deleted = 0\n"
        + "        group by draft_article_id";

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

    String sql = "select team.`name` from team where  is_delete = 0 and ( team_name = ? or name = ? ) and id != ?";

    System.out.println(DM_SQL_PARSER.parse(sql));
  }

  @Test
  public void insertTest() throws SqlTranslateException {
    //String sql = "INSERT INTO `fm_auth_menu`(a,b,c,d,e,f,g) VALUES (92, 'AUTH', '权限管理', 0, 1, 3, NULL);";

    String sql = "INSERT INTO `fm_user_login_record`\n"
        + "   (`user_id`, `ip`, `operate_date`, `type`)\n"
        + "   VALUES\n"
        + " (?, ?, NOW(), ?);";

    System.out.println(DM_SQL_PARSER.parse(sql));
  }

  @Test
  public void dateTest() throws SqlTranslateException {
    String sql = "select  DATE_FORMAT(now(),'%Y%m%d%H%i%s0000')";
    System.out.println(DM_SQL_PARSER.parse(sql));
  }

}
