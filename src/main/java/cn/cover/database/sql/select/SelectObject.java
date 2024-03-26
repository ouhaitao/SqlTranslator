package cn.cover.database.sql.select;

import cn.cover.database.sql.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/26
 * select的对象
 */
@Getter
@Setter
public class SelectObject {
    /**
     * 列
     */
    private Column column;
    
    private String alias;
}
