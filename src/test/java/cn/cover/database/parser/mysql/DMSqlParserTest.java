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



    String sql = "select  \n"
        + "n.id,n.title_list, n.city, n.list_kind,\n"
        + "        n.news_kind,n.img_43,n.img_32,n.img_169,n.img_21,n.img_195,n.img_pc,n.img_85,nd.review_count,nd.reply_count,nd.praise_count,n.source_id,n.is_subject, n.brief, n.date_create,n.date_update, n.author,n.is_cbgc_old_version,n.ar_uniqid\n"
        + ",n.external_url,IF(n.news_reply_strategy=2,0,1) news_reply_strategy\n"
        + " ,n.date_publish, n.common_live_id, n.ext_kind, IFNULL(IFNULL(cs.show_name, cs.name),up.pro_account) AS source, n.author\n"
        + "        ,n.img_list\n"
        + "from fm_news_news n\n"
        + "LEFT JOIN fm_common_source cs ON n.source_id = cs.id\n"
        + "LEFT JOIN fm_news_newsdynamic nd ON n.id=nd.news_id\n"
        + "LEFT JOIN fm_user_prouserprofile up ON n.creator = up.pgc_id";

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
