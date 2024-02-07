package mybatis.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface TestMapper {
    
    Map<Object, Object> select(@Param("id") Integer id);
}
