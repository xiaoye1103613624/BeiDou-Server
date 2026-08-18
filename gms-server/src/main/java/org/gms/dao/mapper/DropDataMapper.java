package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.DropDataDO;
import org.gms.model.dto.MobDropGroupDTO;

import java.util.List;

/**
 * 映射层。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface DropDataMapper extends BaseMapper<DropDataDO> {

    @Select("""
            <script>
            SELECT dropperid AS dropperId, COUNT(*) AS dropCount
            FROM drop_data
            <where>
              <if test="dropperId != null">AND dropperid = #{dropperId}</if>
              <if test="dropperIds != null and dropperIds.size() &gt; 0">
                AND dropperid IN
                <foreach collection="dropperIds" item="id" open="(" separator="," close=")">#{id}</foreach>
              </if>
              <if test="itemId != null">AND itemid = #{itemId}</if>
              <if test="itemIds != null and itemIds.size() &gt; 0">
                AND itemid IN
                <foreach collection="itemIds" item="id" open="(" separator="," close=")">#{id}</foreach>
              </if>
              <if test="questId != null">AND questid = #{questId}</if>
            </where>
            GROUP BY dropperid
            ORDER BY dropperid
            LIMIT #{offset}, #{limit}
            </script>
            """)
    List<MobDropGroupDTO> selectMobGroups(@Param("dropperId") Integer dropperId,
                                          @Param("dropperIds") List<Integer> dropperIds,
                                          @Param("itemId") Integer itemId,
                                          @Param("itemIds") List<Integer> itemIds,
                                          @Param("questId") Integer questId,
                                          @Param("offset") long offset,
                                          @Param("limit") long limit);

    @Select("""
            <script>
            SELECT COUNT(DISTINCT dropperid)
            FROM drop_data
            <where>
              <if test="dropperId != null">AND dropperid = #{dropperId}</if>
              <if test="dropperIds != null and dropperIds.size() &gt; 0">
                AND dropperid IN
                <foreach collection="dropperIds" item="id" open="(" separator="," close=")">#{id}</foreach>
              </if>
              <if test="itemId != null">AND itemid = #{itemId}</if>
              <if test="itemIds != null and itemIds.size() &gt; 0">
                AND itemid IN
                <foreach collection="itemIds" item="id" open="(" separator="," close=")">#{id}</foreach>
              </if>
              <if test="questId != null">AND questid = #{questId}</if>
            </where>
            </script>
            """)
    long countMobGroups(@Param("dropperId") Integer dropperId,
                        @Param("dropperIds") List<Integer> dropperIds,
                        @Param("itemId") Integer itemId,
                        @Param("itemIds") List<Integer> itemIds,
                        @Param("questId") Integer questId);
}
