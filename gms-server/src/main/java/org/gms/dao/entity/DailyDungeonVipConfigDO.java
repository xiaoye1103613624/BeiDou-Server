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
 * 每日副本VIP物品配置实体（玩家持有指定物品可解锁VIP功能）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_dungeon_vip_config")
public class DailyDungeonVipConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** VIP物品ID（玩家持有此物品可启用VIP功能，如直接传送） */
    private Integer itemId;

    /** VIP功能描述 */
    private String description;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 排序顺序（升序） */
    private Integer sortOrder;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
