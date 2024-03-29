package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/26
 * 列之间进行组合
 */
@Getter
@Setter
public class CombinationColumn implements Column {
    
    private Column left;
    
    private Column right;
    
    private Operate operate;
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
    
    public enum Operate {
        /**
         * 值比较
         */
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_EQUAL,
        LESS_THAN,
        LESS_EQUAL,
        LIKE,
        /**
         * 值判断
         */
        IS_NULL,
        IS_NOT_NULL,
        IN,
        NOT_IN,
        /**
         * 值计算
         */
        Addition,
        Division,
        
        /**
         * 逻辑连接词
         */
        AND,
        OR
    }
}
