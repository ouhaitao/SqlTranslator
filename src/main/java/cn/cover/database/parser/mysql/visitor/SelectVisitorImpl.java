package cn.cover.database.parser.mysql.visitor;

import cn.cover.database.sql.SQL;
import cn.cover.database.sql.column.CombinationColumn;
import cn.cover.database.sql.column.NumberColumn;
import cn.cover.database.sql.select.Select;
import cn.cover.database.sql.select.*;
import cn.cover.database.sql.table.JoinObject;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

/**
 * @author parry 2024/01/22
 */
public class SelectVisitorImpl implements SelectVisitor {
    
    private Select select;
    
    public SelectVisitorImpl(Select select) {
        this.select = select;
    }
    
    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.getDistinct() != null) {
            select.addBehaviorObject(SQL.Behavior.DISTINCT);
        }
        // select的列
        SelectItemVisitorImpl selectItemVisitor = new SelectItemVisitorImpl();
        plainSelect.getSelectItems().forEach(item -> {
            item.accept(selectItemVisitor);
            select.addSelectObject(selectItemVisitor.getSelectObject());
        });
        
        // from的表
        FromItemVisitorImpl fromItemVisitor = new FromItemVisitorImpl();
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(fromItemVisitor);
            select.setFromObject(fromItemVisitor.getFromObject());
        }
    
        // join的表
        ExpressionVisitorImpl expressionVisitor = new ExpressionVisitorImpl();
        if (plainSelect.getJoins() != null) {
            plainSelect.getJoins().forEach(join -> {
                JoinObject joinObject = new JoinObject();
                // 表
                join.getRightItem().accept(fromItemVisitor);
                joinObject.setJoinType(getJoinType(join));
                joinObject.setFromObject(fromItemVisitor.getFromObject());
                // on
                join.getOnExpressions().forEach(e -> {
                    e.accept(expressionVisitor);
                    joinObject.addOnColumn((CombinationColumn) expressionVisitor.getColumn());
                });
                select.getFromObject().addJoinObject(joinObject);
            });
        }
        
        // where
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(expressionVisitor);
            select.setWhere((CombinationColumn) expressionVisitor.getColumn());
        }
        
        // groupBy
        if (plainSelect.getGroupBy() != null) {
            plainSelect.getGroupBy()
                .getGroupByExpressionList()
                .getExpressions()
                .forEach(e -> {
                    e.accept(expressionVisitor);
                    GroupByObject groupByObject = new GroupByObject(expressionVisitor.getColumn());
                    select.addGroupByObject(groupByObject);
                });
        }
        
        // orderBy
        if (plainSelect.getOrderByElements() != null) {
            plainSelect.getOrderByElements()
                .forEach(orderByElement -> {
                    orderByElement.getExpression().accept(expressionVisitor);
                    OrderByObject orderByObject = new OrderByObject(expressionVisitor.getColumn(), orderByElement.isAsc());
                    select.addOrderByObject(orderByObject);
                });
        }
        
        // limit
        Limit limit = plainSelect.getLimit();
        if (limit != null) {
            Long offset = null, rowCount;
            if (limit.getOffset() != null) {
                limit.getOffset().accept(expressionVisitor);
                NumberColumn offsetColumn = (NumberColumn) expressionVisitor.getColumn();
                offset = offsetColumn.getValue();
            }
            limit.getRowCount().accept(expressionVisitor);
            NumberColumn rowCountColumn = (NumberColumn) expressionVisitor.getColumn();
            rowCount = rowCountColumn.getValue();
            LimitObject limitObject = new LimitObject(offset, rowCount);
            select.setLimit(limitObject);
        }
    
        // having
        Expression having = plainSelect.getHaving();
        if (having != null) {
            having.accept(expressionVisitor);
            HavingObject havingObject = new HavingObject(expressionVisitor.getColumn());
            select.setHavingObjectList(havingObject);
        }
    }
    
    @Override
    public void visit(SetOperationList setOpList) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(WithItem withItem) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ValuesStatement aThis) {
        throw new UnsupportedOperationException();
    }
    
    public Select getSelect() {
        return select;
    }
    
    public JoinObject.JoinType getJoinType(Join join) {
        if (join.isLeft()) {
            return JoinObject.JoinType.LEFT_JOIN;
        }
        throw new UnsupportedOperationException();
    }
}
