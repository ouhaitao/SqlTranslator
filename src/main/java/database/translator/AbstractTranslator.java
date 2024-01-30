package database.translator;

import database.Translator;
import database.sql.SQL;
import database.sql.select.Select;

/**
 * @author parry 2024/01/22
 * 转译器
 */
public abstract class AbstractTranslator implements Translator {
    
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
    
}
