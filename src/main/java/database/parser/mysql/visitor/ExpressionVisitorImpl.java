package database.parser.mysql.visitor;

import database.sql.Column;
import database.sql.column.*;
import database.sql.column.Function;
import database.sql.select.SubSelect;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;

import java.util.Optional;

/**
 * @author parry 2024/01/22
 */
public class ExpressionVisitorImpl implements ExpressionVisitor {
    
    private Column column;
    
    @Override
    public void visit(BitwiseRightShift aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(BitwiseLeftShift aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(net.sf.jsqlparser.expression.Function function) {
        Function sqlFunction = new Function();
        sqlFunction.setName(function.getName());
    
        ExpressionList parameters = function.getParameters();
        ExpressionVisitorImpl visitor = new ExpressionVisitorImpl();
        Optional.ofNullable(parameters)
            .ifPresent(p -> p.getExpressions().forEach(e -> {
                    e.accept(visitor);
                    sqlFunction.addArg(visitor.getColumn());
                }
            )
        );
        
        column = sqlFunction;
    }
    
    @Override
    public void visit(SignedExpression signedExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(DoubleValue doubleValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(LongValue longValue) {
        column = new NumberColumn(longValue.getValue());
    }
    
    @Override
    public void visit(HexValue hexValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(DateValue dateValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TimeValue timeValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TimestampValue timestampValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
        column = new ParenthesisColumn(getColumn());
    }
    
    @Override
    public void visit(StringValue stringValue) {
        column = new database.sql.column.StringValue(stringValue.getValue());
    }
    
    @Override
    public void visit(Addition addition) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Division division) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IntegerDivision division) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Multiplication multiplication) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Subtraction subtraction) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(AndExpression andExpression) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.AND);
        andExpression.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        andExpression.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(OrExpression orExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(XorExpression orExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Between between) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(EqualsTo equalsTo) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.EQUAL);
        equalsTo.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        equalsTo.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(GreaterThan greaterThan) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.GREATER_THAN);
        greaterThan.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        greaterThan.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.GREATER_EQUAL);
        greaterThanEquals.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        greaterThanEquals.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(FullTextSearch fullTextSearch) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IsNullExpression isNullExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(MinorThan minorThan) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.LESS_THAN);
        minorThan.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        minorThan.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.LESS_EQUAL);
        minorThanEquals.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        minorThanEquals.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        CombinationColumn combinationColumn = new CombinationColumn();
        combinationColumn.setOperate(CombinationColumn.Operate.NOT_EQUAL);
        notEqualsTo.getLeftExpression().accept(this);
        combinationColumn.setLeft(getColumn());
        notEqualsTo.getRightExpression().accept(this);
        combinationColumn.setRight(getColumn());
        column = combinationColumn;
    }
    
    @Override
    public void visit(net.sf.jsqlparser.schema.Column tableColumn) {
        column = new StringColumn(tableColumn.toString());
    }
    
    @Override
    public void visit(net.sf.jsqlparser.statement.select.SubSelect subSelect) {
        SubSelect subSelectSQL = new SubSelect();
        SelectVisitorImpl selectVisitor = new SelectVisitorImpl(subSelectSQL);
        subSelect.getSelectBody().accept(selectVisitor);
        column = subSelectSQL;
    }
    
    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ExistsExpression existsExpression) {
        Function function = new Function();
        if (existsExpression.isNot()) {
            function.setName("NOT EXISTS");
        } else {
            function.setName("EXISTS");
        }
        function.setUseParenthesis(false);
        existsExpression.getRightExpression().accept(this);
        function.addArg(getColumn());
        column = function;
    }
    
    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Matches matches) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(CastExpression cast) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TryCastExpression cast) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(Modulo modulo) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(AnalyticExpression aexpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ExtractExpression eexpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IntervalExpression iexpr) {
        column = new StringColumn(iexpr.toString());
    }
    
    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(RegExpMatchOperator rexpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JsonExpression jsonExpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JsonOperator jsonExpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(UserVariable var) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NumericBind bind) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(KeepExpression aexpr) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ValueListExpression valueList) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(RowConstructor rowConstructor) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(RowGetExpression rowGetExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(OracleHint hint) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(DateTimeLiteralExpression literal) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NotExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(NextValExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(CollateExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(SimilarToExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ArrayExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ArrayConstructor aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(VariableAssignment aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(XMLSerializeExpr aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(TimezoneExpression aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JsonAggregateFunction aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(JsonFunction aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(ConnectByRootOperator aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(OracleNamedFunctionParameter aThis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(AllColumns allColumns) {
        column = new StringColumn(allColumns.toString());
    }
    
    @Override
    public void visit(AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(AllValue allValue) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(IsDistinctExpression isDistinctExpression) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void visit(GeometryDistance geometryDistance) {
        throw new UnsupportedOperationException();
    }
    
    public Column getColumn() {
        return column;
    }
}
