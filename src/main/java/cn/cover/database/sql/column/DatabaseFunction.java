package cn.cover.database.sql.column;

import cn.cover.database.sql.Database;
import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/01
 * Function包装类，带有数据库标识
 */
@Getter
@Setter
public class DatabaseFunction implements Column {
    
    private Function function;
    
    private Database database;
    
    public DatabaseFunction(Function function, Database database) {
        this.function = function;
        this.database = database;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
