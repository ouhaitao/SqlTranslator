package cn.cover.database.translator;

import cn.cover.database.sql.SQL;
import cn.cover.database.sql.column.*;
import cn.cover.database.sql.select.*;
import cn.cover.database.sql.table.FromObject;
import cn.cover.database.sql.table.JoinObject;
import cn.cover.database.sql.Column;
import cn.cover.database.sql.Database;
import database.sql.column.*;
import database.sql.select.*;
import cn.cover.database.sql.table.StringTable;
import cn.cover.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author parry 2024/01/29
 */
public class MySQLTranslator extends AbstractTranslator {
    
    private StringBuilder sb;
    
    public MySQLTranslator() {
    }
    
    @Override
    public String select(Select sql) {
        if (sb == null) {
            this.sb = new StringBuilder();
        }
        String newsSql = select(sql, sb);
        sb = null;
        return newsSql;
    }
    
    private String select(Select sql, final StringBuilder sb) {
        sb.append("select");
        // behavior
        List<SQL.Behavior> behaviorList = CollectionUtils.nonNullAndDefaultEmpty(sql.getBehaviorList());
        behaviorList.forEach(behavior -> sb.append(" ").append(behavior));
        sb.append(" ");
        // selectObject
        List<SelectObject> selectObjectList = CollectionUtils.nonNullAndDefaultEmpty(sql.getSelectObjectList());
        appendSelectObject(selectObjectList);
        // fromObject
        Optional.ofNullable(sql.getFromObject()).ifPresent(fromObject -> {
            sb.append(" from ");
            appendFromObject(fromObject);
        });
        // whereObject
        Optional.ofNullable(sql.getWhere()).ifPresent(where -> {
            sb.append(" where ");
            appendWhereObject(where);
        });
        // groupBy
        Optional.ofNullable(sql.getGroupByObjectList()).ifPresent(groupByObjectList -> {
            sb.append(" group by ");
            appendGroupByObject(groupByObjectList);
        });
        // having
        Optional.ofNullable(sql.getHavingObjectList()).ifPresent(havingObject -> {
            sb.append(" having ");
            appendHavingObject(havingObject);
        });
        // orderBy
        Optional.ofNullable(sql.getOrderByObjectList()).ifPresent(orderByObjectList -> {
            sb.append(" order by ");
            appendOrderByObject(orderByObjectList);
        });
        // limit
        Optional.ofNullable(sql.getLimit()).ifPresent(limitObject -> {
            sb.append(" limit ");
            appendLimitObject(limitObject);
        });
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
    
    private void appendHavingObject(HavingObject havingObject) {
        havingObject.getColumn().accept(this);
    }
    
    @Override
    public void visit(StringColumn stringColumn) {
        sb.append(stringColumn.getColumn());
    }
    
    @Override
    public void visit(CombinationColumn combinationColumn) {
        combinationColumn.getLeft().accept(this);
        sb.append(" ").append(getCombinationColumnOperateString(combinationColumn.getOperate()));
        Optional.ofNullable(combinationColumn.getRight()).ifPresent(right -> {
            sb.append(" ");
            right.accept(this);
        });
    }
    
    @Override
    public void visit(Function function) {
        sb.append(function.getName());
        if (function.isUseParenthesis()) {
            sb.append("(");
        }
        if (function.isDistinct()) {
            sb.append("DISTINCT ");
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
        select(subSelect, sb);
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
    public void visit(CaseColumn caseColumn) {
        sb.append("CASE ");
        Optional.ofNullable(caseColumn.getColumn()).ifPresent(column -> {
            column.accept(this);
            sb.append(" ");
        });
        caseColumn.getWhenColumnList().forEach(column -> column.accept(this));
        Optional.ofNullable(caseColumn.getElseColumn()).ifPresent(column -> {
            sb.append(" ELSE ");
            caseColumn.getElseColumn().accept(this);
        });
        sb.append(" END");
    }
    
    @Override
    public void visit(WhenColumn whenColumn) {
        sb.append("WHEN ");
        whenColumn.getWhenColumn().accept(this);
        sb.append(" THEN ");
        whenColumn.getThenColumn().accept(this);
    }
    
    @Override
    public void visit(JdbcParameterColumn jdbcParameterColumn) {
        sb.append("?");
    }
    
    
    @Override
    public Function translate(DatabaseFunction source) {
        return source.getFunction();
    }
    
    @Override
    public boolean support(DatabaseFunction databaseFunction) {
        return databaseFunction.getDatabase() == Database.MYSQL;
    }
}
