package cn.cover.database.sql.column;

import cn.cover.database.sql.Column;
import cn.cover.database.sql.visitor.ColumnVisitor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/02/07
 * jdbc参数 e.g. id = ?
 */
@Getter
@Setter
public class JdbcParameterColumn implements Column {
    
    private Integer index;
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
