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

    String sql = "SELECT\n"
        + "\tn.id,\n"
        + "\tn.title,\n"
        + "\tn.title_list,\n"
        + "\tn.title_content,\n"
        + "\tn.brief,\n"
        + "\tn.date_create,\n"
        + "\tn.date_publish,\n"
        + "\tn.date_update,\n"
        + "\tn.author,\n"
        + "\tn.city,\n"
        + "\tn.keywords,\n"
        + "\tn.news_kind,\n"
        + "\tn.create_org,\n"
        + "\tn.img_url,\n"
        + "\tn.imgs_url,\n"
        + "\tn.img_face,\n"
        + "\tn.img_channel,\n"
        + "\tn.can_reply,\n"
        + "\tn.STATUS,\n"
        + "\tn.source_id,\n"
        + "\tn.ext_kind,\n"
        + "\tn.list_kind,\n"
        + "\tn.img_43,\n"
        + "\tn.img_169,\n"
        + "\tn.img_32,\n"
        + "\tn.pro_channel,\n"
        + "\tnd.review_count,\n"
        + "\tnd.reply_count,\n"
        + "\tnd.collect_count,\n"
        + "\tnd.praise_count,\n"
        + "\tnd.forward_count,\n"
        + "\tcs.NAME source_str,\n"
        + "\tcs.is_secure whitelist,\n"
        + "\tfcn.NAME publicissue,\n"
        + "\tcn.id linked,\n"
        + "\tcn.channel_id,\n"
        + "\tcn.channel_type,\n"
        + "\tcn.channel_owner,\n"
        + "\tcn.index_no,\n"
        + "\tcn.is_head,\n"
        + "\tcn.is_banner,\n"
        + "\tupp.pro_account,\n"
        + "\tupp.pro_kind,\n"
        + "\tvs.video,\n"
        + "\tnh.id pgc_hotnews_id,\n"
        + "\tifnull( ai.audit_status, 6 ) AS audit_status,\n"
        + "\tai.biz_id \n"
        + "FROM\n"
        + "\tfm_news_news n\n"
        + "\tLEFT JOIN fm_user_prouserprofile upp ON upp.pgc_id = n.creator\n"
        + "\tLEFT JOIN fm_news_channelnews cn ON cn.news_id = n.id \n"
        + "\tAND cn.channel_owner\n"
        + "\tLEFT JOIN fm_common_newschannel nc ON cn.channel_id = nc.id\n"
        + "\tLEFT JOIN fm_news_newsvideo nv ON nv.news_id = n.id\n"
        + "\tLEFT JOIN fm_common_videoset vs ON vs.id = nv.video\n"
        + "\tLEFT JOIN fm_common_newschannel fcn ON fcn.id = n.pro_channel\n"
        + "\tLEFT JOIN fm_news_newsdynamic nd ON nd.news_id = n.id\n"
        + "\tLEFT JOIN fm_common_source cs ON cs.id = n.source_id\n"
        + "\tLEFT JOIN fm_news_ai_audit_buzid ai ON ai.news_id = n.id\n"
        + "\tLEFT JOIN fm_pgc_news_hotnews nh ON nh.news_id = n.id \n"
        + "WHERE\n"
        + "\tn.is_delete = 0 \n"
        + "\tAND create_org = 3 \n"
        + "\tAND n.is_disable = 0 \n"
        + "\tAND (?<= n.date_publish ) \n"
        + "ORDER BY\n"
        + "\tcn.index_no DESC,\n"
        + "\tcn.date_publish DESC \n"
        + "\tLIMIT ?,\n"
        + "\t?";
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
