package cn.cover.database.parser.mysql.visitor.dm.support;

import cn.cover.database.parser.mysql.visitor.dm.Context;
import cn.cover.database.parser.mysql.visitor.dm.DMSelectVisitor;
import com.google.common.base.CharMatcher;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/4/2 16:43
 */
public class FunctionConverter {

  public static boolean convert(final Function function, Context context) {
    final String functionName = function.getName().toUpperCase();
    final SqlAppender sqlBuilder = context.sqlBuild();
    if ("DATE_FORMAT".equalsIgnoreCase(functionName)) {
      final ExpressionList parameters = function.getParameters();
      if (parameters.getExpressions() != null && parameters.getExpressions().size() == 2) {
        boolean isEnd = true;
        String placeholder = "";
        String replaceFormat = "";
        final Expression expression1 = parameters.getExpressions().get(1);
        if (expression1 instanceof StringValue) {
          StringValue exp = (StringValue) expression1;
          final String format = exp.getValue();
          placeholder = CharMatcher.inRange('0', '9').retainFrom(format);
          if (placeholder.isEmpty()) {
            return false;
          }
          isEnd = format.trim().endsWith(placeholder);
          // yyyyMMddHHmmss
          replaceFormat = format.replace(placeholder, "")
              .replace("%Y", "yyyy")
              .replace("%m", "MM")
              .replace("%d", "dd")
              .replace("%H", "HH")
              .replace("%i", "mm")
              .replace("%s", "ss");
        }

        if (replaceFormat.isEmpty()) {
          return false;
        }

        sqlBuilder.appendClose("CONCAT");
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);

        if (!placeholder.isEmpty() && !isEnd) {
          SqlEnum.QUOTE.append(sqlBuilder);
          sqlBuilder.append(placeholder);
          SqlEnum.QUOTE.append(sqlBuilder);
          SqlEnum.COMMA.append(sqlBuilder);
        }

        sqlBuilder.appendClose(functionName);
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
        final Expression expression0 = parameters.getExpressions().get(0);
        DMSelectVisitor.DMExpressionVisitor dmExpressionVisitor = DMSelectVisitor.DMExpressionVisitor.getEnd(context);
        expression0.accept(dmExpressionVisitor);
        SqlEnum.COMMA.append(sqlBuilder);
        SqlEnum.QUOTE.append(sqlBuilder);
        sqlBuilder.appendClose(replaceFormat);
        SqlEnum.QUOTE.append(sqlBuilder);
        SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);


        if (!placeholder.isEmpty() && isEnd) {
          SqlEnum.COMMA.append(sqlBuilder);
          SqlEnum.QUOTE.append(sqlBuilder);
          sqlBuilder.appendClose(placeholder);
          SqlEnum.QUOTE.append(sqlBuilder);
        }
        SqlEnum.RIGHT_PARENTHESIS.append(sqlBuilder);
      }
      return true;
    }
    return false;
  }
}
