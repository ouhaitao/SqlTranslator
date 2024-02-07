package database.sql.column;

import database.sql.Column;
import database.sql.visitor.ColumnVisitor;
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
