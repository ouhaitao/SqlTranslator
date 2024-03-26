package cn.cover.database.sql.table;

import cn.cover.database.sql.column.CombinationColumn;
import lombok.Getter;
import lombok.Setter;
import cn.cover.util.CollectionUtils;

import java.util.List;

/**
 * @author parry 2024/01/26
 * join的对象
 */
@Getter
@Setter
public class JoinObject {
    
    private JoinType joinType;
    
    private FromObject fromObject;
    /**
     * join的条件
     */
    private List<CombinationColumn> onColumnList;
    
    public void addOnColumn(CombinationColumn column) {
        onColumnList = CollectionUtils.nonNull(onColumnList);
        onColumnList.add(column);
    }
    
    /**
     * join类型
     */
    public enum JoinType {
        LEFT_JOIN,
        RIGHT_JOIN,
        INNER_JOIN
    }
}
