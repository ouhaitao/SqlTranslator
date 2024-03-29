package cn.cover.database.sql.column;

import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;
import cn.cover.util.CollectionUtils;

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
    /**
     * 是否使用括号
     */
    private boolean useParenthesis = true;
    
    private boolean distinct = false;
    
    public void addArg(Column SelectColumn) {
        argList = CollectionUtils.nonNull(argList);
        argList.add(SelectColumn);
    }
    
    @Override
    public void accept(ColumnVisitor visitor) {
        visitor.visit(this);
    }
}
