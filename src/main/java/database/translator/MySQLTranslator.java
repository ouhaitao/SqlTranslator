package database.translator;

import database.sql.Column;
import database.sql.Database;
import database.sql.column.*;
import database.sql.select.*;
import database.sql.table.FromObject;
import database.sql.table.JoinObject;
import database.sql.table.StringTable;
import database.sql.visitor.ColumnVisitor;
import database.sql.visitor.TableVisitor;
import util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author parry 2024/01/29
 */
public class MySQLTranslator extends AbstractTranslator {
    
    private final StringBuilder sb;
    
    public MySQLTranslator() {
        sb = new StringBuilder();
    }
    
    @Override
    public String select(Select sql) {
        sb.append("select");
        // behavior
        List<Select.Behavior> behaviorList = CollectionUtils.nonNullAndDefaultEmpty(sql.getBehaviorList());
        behaviorList.forEach(behavior -> sb.append(" ").append(behavior));
        sb.append(" ");
        // selectObject
        List<SelectObject> selectObjectList = CollectionUtils.nonNullAndDefaultEmpty(sql.getSelectObjectList());
        appendSelectObject(selectObjectList);
        // fromObject
        if (sql.getFromObject() != null) {
            sb.append(" from ");
            appendFromObject(sql.getFromObject());
        }
        // whereObject
        if (sql.getWhere() != null) {
            sb.append(" where ");
            appendWhereObject(sql.getWhere());
        }
        // groupBy
        if (sql.getGroupByObjectList() != null) {
            sb.append(" group by ");
            appendGroupByObject(sql.getGroupByObjectList());
        }
        // orderBy
        if (sql.getOrderByObjectList() != null) {
            sb.append(" order by ");
            appendOrderByObject(sql.getOrderByObjectList());
        }
        // limit
        if (sql.getLimit() != null) {
            sb.append(" limit ");
            appendLimitObject(sql.getLimit());
        }
        return sb.toString();
    }
    
    private void appendSelectObject(List<SelectObject> list) {
        Iterator<SelectObject> iterator = list.iterator();
        while (iterator.hasNext()) {
            SelectObject selectObject = iterator.next();
            selectObject.getColumn().accept(this);
            Optional.ofNullable(selectObject.getAlias()).ifPresent(alias -> sb.append(" as ").append(alias));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }
    
    private void appendFromObject(FromObject fromObject) {
        fromObject.getTable().accept(this);
        Optional.ofNullable(fromObject.getAlias()).ifPresent(alias -> sb.append(" ").append(alias));
        List<JoinObject> joinObjectList = CollectionUtils.nonNullAndDefaultEmpty(fromObject.getJoinObjectList());
        joinObjectList.forEach(joinObject -> {
            sb.append(" ").append(getJoinTypeString(joinObject.getJoinType())).append(" ");
            appendFromObject(joinObject.getFromObject());
            sb.append(" on ");
            joinObject.getOnColumnList().forEach(column -> column.accept(this));
        });
    }
    
    private void appendWhereObject(CombinationColumn where) {
        where.accept(this);
    }
    
    private void appendGroupByObject(List<GroupByObject> groupByObjectList) {
        Iterator<GroupByObject> iterator = groupByObjectList.iterator();
        while (iterator.hasNext()) {
            GroupByObject groupByObject = iterator.next();
            groupByObject.getColumn().accept(this);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }
    
    private void appendOrderByObject(List<OrderByObject> orderByObjectList) {
        Iterator<OrderByObject> iterator = orderByObjectList.iterator();
        while (iterator.hasNext()) {
            OrderByObject orderByObject = iterator.next();
            orderByObject.getColumn().accept(this);
            sb.append(" ").append(getOrderTypeString(orderByObject.getOrderType()));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }
    
    private void appendLimitObject(LimitObject limitObject) {
        sb.append(limitObject.getRowCount());
        if (limitObject.getOffset() != null) {
            sb.append(", ").append(limitObject.getOffset());
        }
    }
    
    @Override
    public void visit(StringColumn stringColumn) {
        sb.append(stringColumn.getColumn());
    }
    
    @Override
    public void visit(CombinationColumn combinationColumn) {
        combinationColumn.getLeft().accept(this);
        sb.append(" ").append(getCombinationColumnOperateString(combinationColumn.getOperate())).append(" ");
        combinationColumn.getRight().accept(this);
    }
    
    @Override
    public void visit(Function function) {
        sb.append(function.getName());
        if (function.isUseParenthesis()) {
            sb.append("(");
        }
        List<Column> argList = CollectionUtils.nonNullAndDefaultEmpty(function.getArgList());
        Iterator<Column> iterator = argList.iterator();
        while (iterator.hasNext()) {
            Column arg = iterator.next();
            arg.accept(this);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        if (function.isUseParenthesis()) {
            sb.append(")");
        }
    }
    
    @Override
    public void visit(ParenthesisColumn parenthesisColumn) {
        sb.append("(");
        parenthesisColumn.getColumn().accept(this);
        sb.append(")");
    }
    
    @Override
    public void visit(StringTable stringTable) {
        sb.append(stringTable.getTable());
    }
    
    @Override
    public void visit(SubSelect subSelect) {
        sb.append("(");
        select(subSelect);
        sb.append(")");
    }
    
    @Override
    public void visit(StringValue stringValue) {
        sb.append("'").append(stringValue.getValue()).append("'");
    }
    
    @Override
    public void visit(NumberColumn numberColumn) {
        sb.append(numberColumn.getValue());
    }
    
    @Override
    public void visit(IntervalColumn intervalColumn) {
        sb.append("INTERVAL ").append(intervalColumn.getNum()).append(" ").append(intervalColumn.getUnit());
    }
    
    @Override
    public void visit(ExistsColumn existsColumn) {
        sb.append(existsColumn.isNot() ? "NOT EXISTS" : "EXISTS");
        existsColumn.getColumn().accept(this);
    }
    
    @Override
    public void visit(NotColumn notColumn) {
        sb.append("NOT ");
        notColumn.getColumn().accept(this);
    }
    
    
    @Override
    public Function translate(DatabaseFunction source) {
        return source.getFunction();
    }
    
    @Override
    public boolean support(Database database) {
        if (database == Database.MYSQL) {
            return true;
        }
        return false;
    }
}
