package cn.cover.database.sql.table;

import cn.cover.database.sql.Table;
import cn.cover.database.sql.visitor.TableVisitor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/26
 * 普通的表名
 */
@Getter
@Setter
public class StringTable implements Table {
    
    public StringTable(String table) {
        this.table = table;
    }
    
    private String table;
    
    @Override
    public void accept(TableVisitor visitor) {
        visitor.visit(this);
    }
}
