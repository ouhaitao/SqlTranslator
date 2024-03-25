package database.parser.mysql;

import database.Parser;
import database.parser.mysql.visitor.SelectVisitorImpl;
import database.sql.SQL;
import database.sql.select.Select;
import exception.SqlTranslateException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author parry 2024/01/22
 */
public class MySQLParser implements Parser {
    
    @Override
    public SQL parse(String originSQL) throws SqlTranslateException {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(originSQL);
        } catch (JSQLParserException e) {
            throw new SqlTranslateException(e);
        }
        if (statement instanceof net.sf.jsqlparser.statement.select.Select) {
            return parseSelect((net.sf.jsqlparser.statement.select.Select) statement);
        } else if (statement instanceof net.sf.jsqlparser.statement.update.Update) {
            return parseUpdate((net.sf.jsqlparser.statement.update.Update) statement);
        }
        throw new UnsupportedOperationException();
    }
    
    private SQL parseSelect(net.sf.jsqlparser.statement.select.Select selectStatement) {
        Select select = new Select();
        SelectVisitorImpl selectVisitor = new SelectVisitorImpl(select);
        selectStatement.getSelectBody().accept(selectVisitor);
        return select;
    }
    
    private SQL parseUpdate(net.sf.jsqlparser.statement.update.Update updateStatement) {
        return null;
    }
    
}
