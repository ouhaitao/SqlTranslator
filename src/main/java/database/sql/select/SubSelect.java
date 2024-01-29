package database.sql.select;

import database.sql.Column;
import database.sql.Table;
import database.sql.visitor.ColumnVisitor;
import database.sql.visitor.TableVisitor;

/**
 * @author parry 2024/01/22
 * 子查询
 */
public class SubSelect extends Select implements Column, Table {
    
    public SubSelect() {
        super(Type.SUB_SELECT);
    }
    
    @Override
    public void accept(TableVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
