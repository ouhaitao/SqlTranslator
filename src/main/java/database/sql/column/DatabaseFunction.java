package database.sql.column;

import database.sql.Column;
import database.sql.Database;
import database.sql.visitor.ColumnVisitor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/01
 * Function包装类，带有数据库表示
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
