package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/04
 */
@Getter
@Setter
public class NotColumn implements Column {
    
    private Column column;
    
    public NotColumn(Column column) {
        this.column = column;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
