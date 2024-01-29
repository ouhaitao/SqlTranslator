package database.parser.mysql.visitor;

import database.sql.select.SelectObject;
import database.sql.column.StringColumn;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

/**
 * @author parry 2024/01/22
 */
public class SelectItemVisitorImpl implements SelectItemVisitor {
    
    private SelectObject selectObject;
    
    @Override
    public void visit(AllColumns allColumns) {
        selectObject = new SelectObject();
        selectObject.setColumn(new StringColumn(allColumns.toString()));
    }
    
    @Override
    public void visit(AllTableColumns allTableColumns) {
        selectObject = new SelectObject();
        selectObject.setColumn(new StringColumn(allTableColumns.toString()));
    }
    
    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        ExpressionVisitorImpl visitor = new ExpressionVisitorImpl();
        selectExpressionItem.getExpression().accept(visitor);
        selectObject = new SelectObject();
        selectObject.setColumn(visitor.getColumn());
        if (selectExpressionItem.getAlias() != null) {
            selectObject.setAlias(selectExpressionItem.getAlias().getName());
        }
    }
    
    public SelectObject getSelectObject() {
        return selectObject;
    }
}
