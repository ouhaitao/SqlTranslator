package cn.cover.database.sql.column;

import cn.cover.database.sql.Column;
import cn.cover.database.sql.visitor.ColumnVisitor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/29
 */
@Getter
@Setter
public class NumberColumn implements Column {
    
    private long value;
    
    public NumberColumn(long value) {
        this.value = value;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
