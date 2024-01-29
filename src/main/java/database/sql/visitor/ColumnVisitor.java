package database.sql.visitor;

import database.sql.column.*;
import database.sql.select.SubSelect;


public interface ColumnVisitor {
    
    void visit(StringColumn stringColumn);
    
    void visit(CombinationColumn combinationColumn);
    
    void visit(Function function);
    
    void visit(ParenthesisColumn parenthesisColumn);
    
    void visit(SubSelect subSelect);
    
    void visit(StringValue stringValue);
    
    void visit(NumberColumn numberColumn);
}
