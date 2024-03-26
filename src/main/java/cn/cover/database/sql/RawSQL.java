package cn.cover.database.sql;

import lombok.Getter;

/**
 * @author parry 2024/03/25
 * 不进行转译的SQL
 */
@Getter
public class RawSQL extends SQL {
    
    private final String sql;
    
    public RawSQL(String sql) {
        super(Type.RAW);
        this.sql = sql;
    }
}
