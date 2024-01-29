package database.parser.mysql.visitor;

import database.sql.table.FromObject;
import database.sql.table.StringTable;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author parry 2024/01/26
 */
public class FromItemVisitorImpl implements FromItemVisitor {
    
    private FromObject fromObject;
    
    @Override
    public void visit(Table tableName) {
        fromObject = new FromObject();
        fromObject.setTable(new StringTable(tableName.getName()));
        if (tableName.getAlias() != null) {
            fromObject.setAlias(tableName.getAlias().getName());
        }
    }
    
    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(SubJoin subjoin) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ValuesList valuesList) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TableFunction tableFunction) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ParenthesisFromItem aThis) {
        throw new UnsupportedOperationException();
    }
    
    public FromObject getFromObject() {
        return fromObject;
    }
    
}
