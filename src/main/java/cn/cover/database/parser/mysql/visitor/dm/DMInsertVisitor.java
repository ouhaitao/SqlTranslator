package cn.cover.database.parser.mysql.visitor.dm;

import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlAppender;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlEnum;
import cn.cover.database.parser.mysql.visitor.dm.support.SqlUtil;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/26 17:50
 */
public class DMInsertVisitor {

  final Insert insert;
  final SqlAppender sqlBuilder;
  final Context context;

  public DMInsertVisitor(final Insert insert, Context context) {
    this.insert = insert;
    this.sqlBuilder = context.sqlBuild();
    this.context = context;
  }

  public void visitor() {
    final Table table = insert.getTable();
    SqlEnum.INSERT.append(sqlBuilder);
    SqlEnum.INTO.append(sqlBuilder);
    sqlBuilder.append(SqlUtil.appendTableName(table));
    final List<Column> columns = insert.getColumns();
    if (columns != null && !columns.isEmpty()) {
      //sqlBuilder.append(" (");
      SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
      DMExpressionVisitor.expressionListVisitor(columns, sqlBuilder);
      //sqlBuilder.append(") ");
      SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
    }
    SqlEnum.VALUES.append(sqlBuilder);
    SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
    final ItemsList itemsList = insert.getItemsList();
    itemsList.accept(new DMItemsListVisitor(sqlBuilder));
    SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
  }

  static class DMItemsListVisitor extends ItemsListVisitorAdapter {

    private final SqlAppender stringBuilder;

    public DMItemsListVisitor(final SqlAppender stringBuilder) {
      this.stringBuilder = stringBuilder;
    }

    @Override
    public void visit(final ExpressionList expressionList) {
      final List<Expression> expressions = expressionList.getExpressions();
      DMExpressionVisitor.expressionListVisitor(expressions, stringBuilder);
    }
  }
}
