package cn.cover.database.sql;


import cn.cover.database.sql.visitor.TableVisitor;

public interface Table extends ASTNode {
    
    void accept(TableVisitor visitor);
}
