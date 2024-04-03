package cn.cover.database.parser.mysql.visitor.dm.support;

import cn.cover.database.parser.mysql.visitor.dm.Context;
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



  public static void convert(final Function function, Context context) {
    final String functionName = function.getName().toUpperCase();
    final SqlAppender sqlBuilder = context.sqlBuild();
    if ("DATE_FORMAT".equalsIgnoreCase(functionName)) {
      final ExpressionList parameters = function.getParameters();
      if (parameters.getExpressions() != null && parameters.getExpressions().size() == 2) {
        boolean isEnd = true;
        String s = "";
        final Expression expression = parameters.getExpressions().get(1);
        if (expression instanceof StringValue) {
          StringValue exp = (StringValue) expression;
          final String format = exp.getValue();
          s = CharMatcher.inRange('0', '9').retainFrom(format);
          isEnd = format.trim().endsWith(s);
          // yyyyMMddHHmmss
          String replace = format.replace(s, "")
              .replace("%Y", "yyyy")
              .replace("%m", "MM")
              .replace("%d", "dd")
              .replace("%H", "HH")
              .replace("%i", "mm")
              .replace("%s", "ss");
          exp.setValue(replace);
        }

        if (s != null && s.length() > 0) {
          sqlBuilder.appendClose("CONCAT");
          SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);
          if (isEnd) {

          }
        }
        sqlBuilder.appendClose(functionName);
        SqlEnum.LEFT_PARENTHESIS.append(sqlBuilder);



      }
    }
  }
}
