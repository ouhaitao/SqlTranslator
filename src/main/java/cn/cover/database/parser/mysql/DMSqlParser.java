package cn.cover.database.parser.mysql;

import cn.cover.database.Parser;
import cn.cover.database.parser.mysql.visitor.dm.DMStatementVisitor;
import cn.cover.database.sql.RawSQL;
import cn.cover.database.sql.SQL;
import cn.cover.exception.SqlTranslateException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author parry 2024/03/26
 */
public class DMSqlParser implements Parser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DMSqlParser.class);

  @Override
  public SQL parse(String originSQL) throws SqlTranslateException {
    StringBuilder translateSql = new StringBuilder();
    try {
      Statement stmt = CCJSqlParserUtil.parse(originSQL);
      if (stmt instanceof Select) {
        Select select = (Select) stmt;
        select.accept(new DMStatementVisitor(translateSql));
      }
    } catch (JSQLParserException e) {
      LOGGER.info("J");
    }
    LOGGER.info("转换之后的sql: {}", translateSql);
    return new RawSQL(translateSql.toString());
  }
}
