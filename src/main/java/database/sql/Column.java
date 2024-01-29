package database.sql;

import database.sql.visitor.ColumnVisitor;

/**
 * @author parry 2024/01/22
 * 列
 */
public interface Column extends ASTNode {
    
    void accept(ColumnVisitor visitor);
}
