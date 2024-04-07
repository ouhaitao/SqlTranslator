package cn.cover.database.parser.mysql;

import cn.cover.database.Parser;
import cn.cover.database.parser.mysql.visitor.dm.Context;
import cn.cover.database.parser.mysql.visitor.dm.ContextImpl;
import cn.cover.database.parser.mysql.visitor.dm.DMStatementVisitor;
import cn.cover.database.sql.RawSQL;
import cn.cover.database.sql.SQL;
import cn.cover.exception.SqlTranslateException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author parry 2024/03/26
 */
public class DMSqlParser implements Parser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DMSqlParser.class);

  @Override
  public SQL parse(String originSQL) throws SqlTranslateException {
    try {
      Statement stmt = CCJSqlParserUtil.parse(originSQL);
      Context context = ContextImpl.newInstance(originSQL);
      stmt.accept(new DMStatementVisitor(context));
      final String sql = context.sqlBuild().toString();
      LOGGER.info("原始的sql：{}。转换之后的sql: {}", originSQL, sql);
      return new RawSQL(sql);
    } catch (Exception e) {
      LOGGER.info("Translate sql={}解析异常。", originSQL, e);
      throw new SqlTranslateException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    final DMSqlParser parse = new DMSqlParser();
    String txt = " SELECT\n"
        + "         a.id id,\n"
        + "        a.action action,\n"
        + "         a.action_name actoinName,\n"
        + "       a.action_type\n"
        + "         actionType,\n"
        + "         a.parent_id parentId,\n"
        + "       a.module_id moduleId\n"
        + "        FROM\n"
        + "         fm_user_actions a\n"
        + "        LEFT JOIN\n"
        + "        fm_user_moduledict m ON a.module_id=m.id";
    for (int i = 0; i < 10; i++) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final RawSQL parse1 = (RawSQL) parse.parse(txt);
            System.out.println(Thread.currentThread().getName() + " : " + parse1.getSql());
          } catch (SqlTranslateException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();
    }
  }
}
