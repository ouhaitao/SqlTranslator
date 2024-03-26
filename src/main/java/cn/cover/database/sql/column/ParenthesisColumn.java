package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/26
 * 被括号包围的列
 */
@Getter
@Setter
public class ParenthesisColumn implements Column {
    
    private Column column;
    
    public ParenthesisColumn(Column column) {
        this.column = column;
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
