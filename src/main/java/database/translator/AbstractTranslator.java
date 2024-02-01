package database.translator;

import database.FunctionTranslator;
import database.Translator;
import database.sql.SQL;
import database.sql.column.CombinationColumn;
import database.sql.column.DatabaseFunction;
import database.sql.column.Function;
import database.sql.select.OrderByObject;
import database.sql.select.Select;
import database.sql.table.JoinObject;
import database.sql.visitor.ColumnVisitor;
import database.sql.visitor.TableVisitor;

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
                throw new IllegalStateException("错误的SQL类型");
        }
    }
    
    @Override
    public void visit(DatabaseFunction databaseFunction) {
        if (!support(databaseFunction.getDatabase())) {
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
