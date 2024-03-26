package cn.cover.database.sql.select;

import cn.cover.database.sql.SQL;
import lombok.Getter;
import lombok.Setter;
import cn.cover.util.CollectionUtils;

import java.util.List;

/**
 * @author parry 2024/01/22
 */
@Getter
@Setter
public class Select extends SQL {
    
    /**
     * 查询的列
     */
    private List<SelectObject> selectObjectList;
    
    private List<GroupByObject> groupByObjectList;
    
    private List<OrderByObject> orderByObjectList;
    
    private LimitObject limit;
    
    private HavingObject havingObjectList;
    
    public Select() {
        this(Type.SELECT);
    }
    
    protected Select(Type type) {
        super(type);
    }
    
    public void addSelectObject(SelectObject selectObject) {
        selectObjectList = CollectionUtils.nonNull(selectObjectList);
        selectObjectList.add(selectObject);
    }
    
    public void addGroupByObject(GroupByObject groupByObject) {
        groupByObjectList = CollectionUtils.nonNull(groupByObjectList);
        groupByObjectList.add(groupByObject);
    }
    
    public void addOrderByObject(OrderByObject orderByObject) {
        orderByObjectList = CollectionUtils.nonNull(orderByObjectList);
        orderByObjectList.add(orderByObject);
    }
    
}
