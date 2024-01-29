package database.sql.visitor;

import database.sql.select.SubSelect;
import database.sql.table.StringTable;

public interface TableVisitor {
    
    void visit(StringTable stringTable);
    
    void visit(SubSelect subSelect);
}
