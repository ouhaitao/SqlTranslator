package cn.cover.database.parser.mysql.visitor.dm.support;

import cn.cover.database.parser.mysql.visitor.dm.Context;
import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor.DMExpressionVisitor;
import java.util.Collection;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/3/29 18:01
 */
public class SqlUtil {

  // sql分隔符
  public static final String WHITE_SPACE = " ";

  // 空白符
  public static final String BLANK = "";

  // 逗号
  public static final String COMMA = ",";

  // 双引号
  public static final String DOUBLE_QUOTE = "\"";

  // 双引号
  public static final String QUOTE = "'";

  // 反引号
  public static final String BACKTICK = "`";

  public static final String DOT = ".";

  public static StringBuilder delimiter(StringBuilder sqlBuild) {
    if (sqlBuild != null) {
      sqlBuild.append(WHITE_SPACE);
    }
    return sqlBuild;
  }

  public static StringBuilder appendSqlEnum(StringBuilder sqlBuild, SqlEnum sqlEnum) {
    if (sqlBuild != null) {
      sqlBuild.append(sqlEnum.name()).append(WHITE_SPACE);
    }
    return sqlBuild;
  }

  public static String appendTableName(Table table) {
    if (table == null) {
      return null;
    }
    StringBuilder sqlBuilder = new StringBuilder();
    String tableName = table.getName().replaceAll(BACKTICK, "");
    //final Collection<String> tableAliasCol = context.getContext().getTableAlias(tableName);
    //if (tableAliasCol != null && !tableAliasCol.isEmpty()) {
      sqlBuilder.append(CommonVisitor.dealKeyword(tableName.toUpperCase())).append(WHITE_SPACE);
    //} else {
    //  sqlBuilder.append(CommonVisitor.dealKeyword(tableName.toUpperCase())).append(WHITE_SPACE);
    //}
    if (table.getAlias() != null) {
      String tableAlias = table.getAlias().getName();
      sqlBuilder.append(tableAlias).append(" ");
    }
    return sqlBuilder.toString();
  }

  /**
   * 为
   * @param table
   * @param context
   * @return
   */
  public static String tablePrefixForColumn(Table table, Context context) {
    if (table == null) {
      return null;
    }
    final String tableName = table.getName();
    StringBuilder sqlBuilder = new StringBuilder();
    final Collection<String> tableAlias = context.getContext().getTableAlias(tableName);
    if (tableAlias != null && !tableAlias.isEmpty()) {
      sqlBuilder.append(CommonVisitor.dealKeyword(tableName.toUpperCase()));
      SqlEnum.DOT.append(sqlBuilder);
    } else {
      sqlBuilder.append(tableName);
      SqlEnum.DOT.append(sqlBuilder);
    }
    return sqlBuilder.toString();
  }


  public static void extractTableName(Table table, Context context) {
    if (table == null) {
      return;
    }
    String tableName = table.getName().replaceAll(BACKTICK, "");
    String tableAlias = "";
    if (table.getAlias() != null) {
      tableAlias = table.getAlias().getName();

    }
    context.putTable(tableName, tableAlias);
  }

  //
  public static void expressionListVisitor(final Collection<? extends Expression> expressions,
      Context context) {
    int idx = 0;
    int size = expressions.size() - 1;
    for (final Expression onExpression : expressions) {
      if (idx == size) {
        onExpression.accept(DMExpressionVisitor.getEnd(context));
      } else {
        onExpression.accept(DMExpressionVisitor.getNotEnd(context));
      }
      idx++;
    }
  }
}
