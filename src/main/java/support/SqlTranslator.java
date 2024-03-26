package support;

import database.Parser;
import database.parser.mysql.MySQLParser;
import database.parser.mysql.RawParser;
import database.sql.Database;
import database.sql.SQL;
import database.translator.AbstractTranslator;
import database.translator.MySQLTranslator;
import database.translator.RawSqlTranslator;
import exception.SqlTranslateException;
import lombok.Getter;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.HashSet;
import java.util.Set;

/**
 * @author parry 2024/03/13
 * SQL转译入口
 */
@Getter
public class SqlTranslator {
    
    /**
     * 不转译的mapperId {@link MappedStatement#getId()}
     */
    private final Set<String> ignoreMapperIdSet;
    /**
     * 源数据库类型
     */
    private final Database originDatabase;
    /**
     * 目标数据库类型
     */
    private final Database targetDatabase;
    
    private final ThreadLocal<Parser> parserFactory;
    
    private final ThreadLocal<AbstractTranslator> translatorFactory;
    
    private SqlTranslator(Builder builder) {
        ignoreMapperIdSet = builder.ignoreMapperIdSet;
        originDatabase = builder.originDatabase;
        targetDatabase = builder.targetDatabase;
        parserFactory = ThreadLocal.withInitial(this::newParser);
        translatorFactory = ThreadLocal.withInitial(this::newTranslator);
    }
    
    public String translate(String originSql, String mapperId) throws SqlTranslateException {
        if (ignoreMapperIdSet.contains(mapperId)) {
            return originSql;
        }
        Parser parser = parserFactory.get();
        AbstractTranslator translator = translatorFactory.get();
        SQL sql = parser.parse(originSql);
        return translator.translate(sql);
    }
    
    private Parser newParser() {
        switch (originDatabase) {
            case RAW:
                return new RawParser();
            case MYSQL:
                return new MySQLParser();
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    private AbstractTranslator newTranslator() {
        switch (targetDatabase) {
            case RAW:
                return new RawSqlTranslator();
            case MYSQL:
                return new MySQLTranslator();
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private Set<String> ignoreMapperIdSet;
        private Database originDatabase;
        private Database targetDatabase;
        
        private Builder() {
            ignoreMapperIdSet = new HashSet<>();
        }
        
        public Builder addIgnoreMapperId(String mapperId) {
            ignoreMapperIdSet.add(mapperId);
            return this;
        }
        
        public Builder originDatabase(Database originDatabase) {
            this.originDatabase = originDatabase;
            return this;
        }
    
        public Builder targetDatabase(Database targetDatabase) {
            this.targetDatabase = targetDatabase;
            return this;
        }
    
        public SqlTranslator build() {
            return new SqlTranslator(this);
        }
    }
}
