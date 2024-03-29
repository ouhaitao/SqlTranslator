package cn.cover.database.parser.mysql;

import cn.cover.database.Parser;
import cn.cover.database.parser.mysql.visitor.dm.Context;
import cn.cover.database.parser.mysql.visitor.dm.ContextImpl;
import cn.cover.database.parser.mysql.visitor.dm.DMStatementVisitor;
import cn.cover.database.sql.RawSQL;
import cn.cover.database.sql.SQL;
import cn.cover.exception.SqlTranslateException;
import net.sf.jsqlparser.JSQLParserException;
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
    StringBuilder translateSql = new StringBuilder();
    LOGGER.info("原始的sql: {}", originSQL);
    try {
      Statement stmt = CCJSqlParserUtil.parse(originSQL);
      Context context = new ContextImpl();
      context.getContext().setSqlBuilder(translateSql);
      context.getContext().setOriginSql(originSQL);
      stmt.accept(new DMStatementVisitor(context));
    } catch (JSQLParserException e) {
      LOGGER.info("jsqlparser解析异常。", e);
    }
    LOGGER.info("转换之后的sql: {}", translateSql);
    return new RawSQL(translateSql.toString());
  }
}
