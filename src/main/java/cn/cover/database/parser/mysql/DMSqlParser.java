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
    try {
      Statement stmt = CCJSqlParserUtil.parse(originSQL);
      Context context = ContextImpl.newInstance(originSQL);
      stmt.accept(new DMStatementVisitor(context));
      final String sql = context.sqlBuild().toString();
      LOGGER.info("原始的sql：{}。\n转换之后的sql: {}", originSQL, sql);
      return new RawSQL(sql);
    } catch (JSQLParserException e) {
      LOGGER.info("jsqlparser解析异常。", e);
      throw new SqlTranslateException(e.getMessage());
    }
  }
}
