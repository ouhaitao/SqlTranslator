package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/26
 * 普通的列名
 */
@Getter
@Setter
public class StringColumn implements Column {
    
    private String column;
    
    public StringColumn(String column) {
        this.column = column;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
