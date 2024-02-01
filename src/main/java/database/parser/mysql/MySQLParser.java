package database.parser.mysql;

import database.Parser;
import database.parser.mysql.visitor.FromItemVisitorImpl;
import database.parser.mysql.visitor.SelectVisitorImpl;
import database.sql.SQL;
import database.sql.select.Select;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.SelectBody;

/**
 * @author parry 2024/01/22
 */
public class MySQLParser implements Parser {
    
    @Override
    public SQL parse(String originSQL) {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(originSQL);
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return null;
        }
        if (statement instanceof net.sf.jsqlparser.statement.select.Select) {
            return parseSelect((net.sf.jsqlparser.statement.select.Select) statement);
        }
        throw new UnsupportedOperationException();
    }
    
    private SQL parseSelect(net.sf.jsqlparser.statement.select.Select selectStatement) {
        Select select = new Select();
        SelectVisitorImpl selectVisitor = new SelectVisitorImpl(select);
        selectStatement.getSelectBody().accept(selectVisitor);
        return select;
    }
    
}
