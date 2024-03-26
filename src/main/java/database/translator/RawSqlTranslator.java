package database.translator;

import database.sql.SQL;
import database.sql.RawSQL;
import database.sql.column.*;
import database.sql.select.Select;
import database.sql.select.SubSelect;
import database.sql.table.StringTable;

/**
 * @author parry 2024/03/25
 * StringSql的转译器
 */
public class RawSqlTranslator extends AbstractTranslator {
    
    @Override
    public String translate(SQL originSQL) {
        if (originSQL.getType() == SQL.Type.RAW) {
            return ((RawSQL) originSQL).getSql();
        }
        throw new UnsupportedOperationException("不支持的sql类型:" + originSQL.getType());
    }
    
    @Override
    public Function translate(DatabaseFunction source) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean support(DatabaseFunction databaseFunction) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String select(Select sql) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(StringColumn stringColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(CombinationColumn combinationColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Function function) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ParenthesisColumn parenthesisColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(StringValue stringValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NumberColumn numberColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IntervalColumn intervalColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ExistsColumn existsColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NotColumn notColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(CaseColumn caseColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(WhenColumn whenColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JdbcParameterColumn jdbcParameterColumn) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(StringTable stringTable) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException();
    }
}
