package cn.cover.support;


import cn.cover.database.sql.Database;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author parry 2024/03/26
 */
@ConfigurationProperties(prefix = "sql.translator")
public class SqlTranslatorProperties {
    
    /**
     * 源数据库类型
     */
    private Database originDatabase;
    /**
     * 目标数据库类型
     */
    private Database targetDatabase;
    
    private Set<String> ignoreMapperIdSet;
    
    public Database getOriginDatabase() {
        return originDatabase;
    }
    
    public void setOriginDatabase(Database originDatabase) {
        this.originDatabase = originDatabase;
    }
    
    public Database getTargetDatabase() {
        return targetDatabase;
    }
    
    public void setTargetDatabase(Database targetDatabase) {
        this.targetDatabase = targetDatabase;
    }
    
    public Set<String> getIgnoreMapperIdSet() {
        return ignoreMapperIdSet;
    }
    
    public void setIgnoreMapperIdSet(Set<String> ignoreMapperIdSet) {
        this.ignoreMapperIdSet = ignoreMapperIdSet;
    }
}
