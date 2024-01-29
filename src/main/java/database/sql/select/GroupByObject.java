package database.sql.select;

import database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/29
 */
@Getter
@Setter
public class GroupByObject {
    
    private Column column;
    
    public GroupByObject(Column column) {
        this.column = column;
    }
}
