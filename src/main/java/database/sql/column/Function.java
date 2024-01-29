package database.sql.column;

import database.sql.Column;
import database.sql.visitor.ColumnVisitor;
import lombok.Getter;
import lombok.Setter;
import util.CollectionUtils;

import java.util.List;

/**
 * @author parry 2024/01/22
 * 函数
 */
@Getter
@Setter
public class Function implements Column {
    /**
     * 函数名
     */
    private String name;
    /**
     * 参数
     */
    private List<Column> argList;
    
    public void addArg(Column SelectColumn) {
        argList = CollectionUtils.nonNull(argList);
        argList.add(SelectColumn);
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
