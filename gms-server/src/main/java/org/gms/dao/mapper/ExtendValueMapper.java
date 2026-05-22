package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.ExtendValueDO;

import java.sql.Date;
import java.util.List;

/**
 * 【映射器】ExtendValueMapper（interface），包 {@code org.gms.dao.mapper}。
 *
 * 对应数据库表 extend_value 的数据访问接口，提供扩展字段相关查询操作。
 *
 * @author 萧曵
 */
public interface ExtendValueMapper extends BaseMapper<ExtendValueDO> {
    @Delete("delete from extend_value where extend_type = #{extendType} and create_time < #{createTime}")
    void clean(@Param("extendType") String extendType, @Param("createTime") Date createTime);
}
