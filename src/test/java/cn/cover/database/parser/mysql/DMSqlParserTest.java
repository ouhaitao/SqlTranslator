package cn.cover.database.parser.mysql;

import cn.cover.exception.SqlTranslateException;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 14:35
 */
public class DMSqlParserTest {

  public static void main(String[] args) throws SqlTranslateException {
    DMSqlParser parser = new DMSqlParser();
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
    System.out.println(parser.parse(sql));
  }
}
