package cn.cover.database.sql.visitor;

import cn.cover.database.sql.select.SubSelect;
import cn.cover.database.sql.table.StringTable;

public interface TableVisitor {
    
    void visit(StringTable stringTable);
    
    void visit(SubSelect subSelect);
}
