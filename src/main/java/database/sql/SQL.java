package database.sql;

import database.sql.column.CombinationColumn;
import database.sql.select.Select;
import database.sql.table.FromObject;
import lombok.Getter;
import lombok.Setter;
import util.CollectionUtils;

import java.util.List;

/**
 * @author parry 2024/01/22
 */
@Getter
@Setter
public abstract class SQL {
    
    private final Type type;
    /**
     * 查询的行为
     * e.g. DISTINCT, SQL_NO_CACHE, LOW_PRIORITY
     */
    private List<Behavior> behaviorList;
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
    
    public void addBehaviorObject(Behavior behavior) {
        behaviorList = CollectionUtils.nonNull(behaviorList);
        behaviorList.add(behavior);
    }
    
    /**
     * SQL类型
     */
    public enum Type {
        CREATE,
        UPDATE,
        DELETE,
        SELECT,
        SUB_SELECT,
        STRING
    }
    
    public enum Behavior {
        DISTINCT,
        SQL_NO_CACHE
    }
}
