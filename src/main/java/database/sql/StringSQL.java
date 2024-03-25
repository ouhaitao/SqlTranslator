package database.sql;

import lombok.Getter;

/**
 * @author parry 2024/03/25
 * 不进行转译的SQL
 */
@Getter
public class StringSQL extends SQL {
    
    private final String sql;
    
    public StringSQL(String sql) {
        super(Type.STRING);
        this.sql = sql;
    }
}
