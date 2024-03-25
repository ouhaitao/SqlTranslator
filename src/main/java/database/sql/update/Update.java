package database.sql.update;

import database.sql.SQL;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author parry 2024/03/18
 */
@Getter
@Setter
public class Update extends SQL {
    
    public Update() {
        super(Type.UPDATE);
    }
    
    private List<UpdateSetObject> updateSetObjectList;
    
}
