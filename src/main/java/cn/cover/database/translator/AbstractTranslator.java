package cn.cover.database.translator;

import cn.cover.database.FunctionTranslator;
import cn.cover.database.Translator;
import cn.cover.database.sql.SQL;
import cn.cover.database.sql.column.CombinationColumn;
import cn.cover.database.sql.column.DatabaseFunction;
import cn.cover.database.sql.column.Function;
import cn.cover.database.sql.select.OrderByObject;
import cn.cover.database.sql.select.Select;
import cn.cover.database.sql.table.JoinObject;
import cn.cover.database.sql.visitor.ColumnVisitor;
import cn.cover.database.sql.visitor.TableVisitor;

/**
 * @author parry 2024/01/22
 * 转译器
 */
public abstract class AbstractTranslator implements Translator, FunctionTranslator, TableVisitor, ColumnVisitor {
    
    /**
     * 转译SQL
     */
    public String translate(SQL originSQL) {
        switch (originSQL.getType()) {
            case SELECT:
                return select((Select) originSQL);
            default:
                throw new UnsupportedOperationException("不支持的SQL类型:" + originSQL.getType());
        }
    }
    
    @Override
    public void visit(DatabaseFunction databaseFunction) {
        if (!support(databaseFunction)) {
            throw new UnsupportedOperationException("不支持的函数:" + databaseFunction.getDatabase() + "." + databaseFunction.getFunction().getName());
        }
        Function translate = translate(databaseFunction);
        visit(translate);
    }
    
    protected String getCombinationColumnOperateString(CombinationColumn.Operate operate) {
        switch (operate) {
            case EQUAL:
                return "=";
            case NOT_EQUAL:
                return "!=";
            case GREATER_THAN:
                return ">";
            case GREATER_EQUAL:
                return ">=";
            case LESS_THAN:
                return "<";
            case LESS_EQUAL:
                return "<=";
            case LIKE:
                return "LIKE";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case IS_NULL:
                return "IS NULL";
            case IS_NOT_NULL:
                return "IS NOT NULL";
            case IN:
                return "IN";
            case NOT_IN:
                return "NOT IN";
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    protected String getJoinTypeString(JoinObject.JoinType joinType) {
        switch (joinType) {
            case LEFT_JOIN:
                return "LEFT JOIN";
            case INNER_JOIN:
                return "INNER JOIN";
            case RIGHT_JOIN:
                return "RIGHT JOIN";
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    protected String getOrderTypeString(OrderByObject.OrderType orderType) {
        switch (orderType) {
            case ASC:
                return "ASC";
            case DESC:
                return "DESC";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
