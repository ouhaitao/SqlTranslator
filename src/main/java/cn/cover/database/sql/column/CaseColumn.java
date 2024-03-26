package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;
import cn.cover.util.CollectionUtils;

import java.util.List;

/**
 * @author parry 2024/02/07
 */
@Getter
@Setter
public class CaseColumn implements Column {
    
    private Column column;
    
    private List<WhenColumn> whenColumnList;
    
    private Column elseColumn;
    
    public void addWhenColumn(WhenColumn whenColumn) {
        whenColumnList = CollectionUtils.nonNull(this.whenColumnList);
        whenColumnList.add(whenColumn);
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
