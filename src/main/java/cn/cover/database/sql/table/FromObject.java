package cn.cover.database.sql.table;

import cn.cover.database.sql.Table;
import lombok.Getter;
import lombok.Setter;
import cn.cover.util.CollectionUtils;

import java.util.List;

/**
 * from的对象
 */
@Getter
@Setter
public class FromObject {
    
    private Table table;
    
    private String alias;
    
    /**
     * join的表
     */
    private List<JoinObject> joinObjectList;
    
    public void addJoinObject(JoinObject joinObject) {
        joinObjectList = CollectionUtils.nonNull(joinObjectList);
        joinObjectList.add(joinObject);
    }
}
