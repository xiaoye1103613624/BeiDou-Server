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
 * 每日副本配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_dungeon_config")
public class DailyDungeonConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 副本键值（脚本唯一标识） */
    private String dungeonKey;

    /** 副本显示名称 */
    private String dungeonName;

    /** 副本地图ID */
    private Integer mapId;

    /** 地图名称（WZ自动解析，展示用） */
    private String mapName;

    /** 每日需完成次数 */
    private Integer completeCount;

    /** 扫荡券道具ID（0=不可扫荡） */
    private Integer sweepItemId;

    /** 单次扫荡消耗道具数量 */
    private Integer sweepItemCost;

    /** 每日扫荡上限（0=不可扫荡） */
    private Integer maxSweep;

    /** 排序顺序（升序） */
    private Integer sortOrder;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
