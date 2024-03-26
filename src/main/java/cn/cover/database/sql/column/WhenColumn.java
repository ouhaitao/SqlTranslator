package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/07
 */
@Getter
@Setter
public class WhenColumn implements Column {
    
    private Column whenColumn;
    
    private Column thenColumn;
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
