package cn.cover.database.sql.select;

import cn.cover.database.sql.Table;
import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.visitor.TableVisitor;
import cn.cover.database.sql.Column;

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
