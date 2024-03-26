package cn.cover.database.sql.select;


import lombok.Getter;
import lombok.Setter;

/**
 * @author parry 2024/01/29
 */
@Getter
@Setter
public class LimitObject {

    private Long offset;
    
    private Long rowCount;
    
    public LimitObject(Long offset, Long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
    }
}
