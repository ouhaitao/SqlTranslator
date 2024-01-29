package database.sql;

import database.sql.column.CombinationColumn;
import database.sql.table.FromObject;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/22
 */
@Getter
@Setter
public abstract class SQL {
    
    private final Type type;
    /**
     * from的表
     */
    private FromObject fromObject;
    /**
     * 条件
     */
    private CombinationColumn where;
    
    protected SQL(Type type) {
        assert type != null;
        this.type = type;
    }
    
    /**
     * SQL类型
     */
    public enum Type {
        CREATE,
        UPDATE,
        DELETE,
        SELECT,
        SUB_SELECT
    }
}
