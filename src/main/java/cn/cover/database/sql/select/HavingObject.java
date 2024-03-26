package cn.cover.database.sql.select;


import cn.cover.database.sql.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/29
 */
@Getter
@Setter
@AllArgsConstructor
public class HavingObject {
    
    private Column column;
}
