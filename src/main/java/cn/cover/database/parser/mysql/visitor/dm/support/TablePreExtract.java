package cn.cover.database.parser.mysql.visitor.dm.support;

import cn.cover.database.parser.mysql.visitor.dm.Context;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;

/**
 * @Use
 * @Author: jeff
 * @Date: 2024/4/1 14:15
 */
public class TablePreExtract {

  private final Context context;

  public TablePreExtract(final Context context) {
    this.context = context;
  }

  public void extract(PlainSelect plainSelect) {
    final FromItem fromItem = plainSelect.getFromItem();
    if (fromItem == null) {
      return;
    }
    fromItem.accept(new PreFromItemVisitor(context));
    final List<Join> joins = plainSelect.getJoins();
    if (joins != null && !joins.isEmpty()) {
      for (final Join join : joins) {
        final FromItem rightItem = join.getRightItem();
        if (rightItem instanceof Table) {
          Table table = (Table) rightItem;
          SqlUtil.extractTableName(table, context);
        }
      }
    }

  }

  static class PreFromItemVisitor extends FromItemVisitorAdapter {

    private final Context context;

    public PreFromItemVisitor(final Context context) {
      this.context = context;
    }

    @Override
    public void visit(final Table table) {
      SqlUtil.extractTableName(table, context);
    }

    @Override
    public void visit(final SubSelect subSelect) {
      //super.visit(subSelect);
    }

    @Override
    public void visit(final SubJoin subjoin) {
      //super.visit(subjoin);
    }

    @Override
    public void visit(final LateralSubSelect lateralSubSelect) {
      //super.visit(lateralSubSelect);
    }

    @Override
    public void visit(final ValuesList valuesList) {
      //super.visit(valuesList);
    }

    @Override
    public void visit(final TableFunction valuesList) {
      //super.visit(valuesList);
    }

    @Override
    public void visit(final ParenthesisFromItem aThis) {
      //super.visit(aThis);
    }
  }
}
