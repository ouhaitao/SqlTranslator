package cn.cover.database.sql.column;

import cn.cover.database.sql.Column;
import cn.cover.database.sql.visitor.ColumnVisitor;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/**
 * @author parry 2024/02/02
 */
@Getter
@Setter
public class IntervalColumn implements Column {
    
    private int num;
    
    private Unit unit;
    
    public IntervalColumn(int num, String unitStr) {
        this(num, getUnit(unitStr));
    }
    
    public IntervalColumn(int num, Unit unit) {
        this.num = num;
        this.unit = unit;
    }
    
    public static Unit getUnit(String unitStr) {
        return Unit.valueOf(unitStr.toUpperCase(Locale.ROOT));
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
    
    /**
     * 单位
     */
    public enum Unit {
        HOUR,
        DAY
    }
}
