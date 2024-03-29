package mybatis.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface TestMapper {
    
    /**
     * mysql
     */
    Map<Object, Object> mysqlSelect(@Param("id") Integer id);
    
    /**
     * 达梦
     */
    Map<Object, Object> dmSelect(@Param("id") Integer id);
}
