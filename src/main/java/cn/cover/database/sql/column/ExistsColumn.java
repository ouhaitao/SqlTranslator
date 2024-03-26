package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/02
 */
@Getter
@Setter
public class ExistsColumn implements Column {
    
    private boolean not;
    
    private Column column;
    
    public ExistsColumn(Column column) {
        this(false, column);
    }
    
    public ExistsColumn(boolean not, Column column) {
        this.not = not;
        this.column = column;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
