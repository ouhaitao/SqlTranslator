package database.sql;


import database.sql.visitor.TableVisitor;

public interface Table extends ASTNode {
    
    void accept(TableVisitor visitor);
}
