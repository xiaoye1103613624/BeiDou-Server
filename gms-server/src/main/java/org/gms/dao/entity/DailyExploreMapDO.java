package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 每日探索地图池实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_explore_map")
public class DailyExploreMapDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 目标地图ID */
    private Integer mapId;

    /** 地图名称（服务端自动解析） */
    private String mapName;

    /** 地图描述（配置者填写） */
    private String description;

    /** 地图渲染图片（base64 data URL，从maplestory.io爬取缓存） */
    private String mapImage;

    /** 排序顺序（升序） */
    private Integer sortOrder;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
