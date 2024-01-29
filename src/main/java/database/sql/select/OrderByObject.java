package database.sql.select;

import database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/29
 */
@Getter
@Setter
public class OrderByObject {
    
    private Column column;
    
    private OrderType orderType;
    
    public OrderByObject(Column column, boolean isAsc) {
        this(column, isAsc ? OrderByObject.OrderType.ASC : OrderByObject.OrderType.DESC);
    }
    
    public OrderByObject(Column column, OrderType orderType) {
        this.column = column;
        this.orderType = orderType;
    }
    
    public enum OrderType {
        ASC,
        DESC
    }
}
