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
 * 赞助档位奖励明细
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_reward")
public class SponsorRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /** 所属档位 */
    private Integer configId;

    /** nx / maple / meso / item / skill_group */
    private String type;

    /** 道具ID（item 时有效） */
    private Integer itemId;

    /**
     * 数量。
     * skill_group + MULTI：玩家需选取的技能个数；ONE 固定为 1；ALL 忽略。
     */
    private Integer qty;

    /**
     * 装备属性模式：default=WZ模板 / custom=自定义。
     * 非装备道具忽略；null 视为 default。
     */
    private String statMode;

    /**
     * 自定义装备属性 JSON（stat_mode=custom 时有效）。
     * 字段：str/dex/int/luk/hp/mp/pAtk/mAtk/pDef/mDef/acc/avoid/hands/speed/jump/upgradeSlot
     */
    private String statsJson;

    /**
     * skill_group 选取模式：ONE / MULTI / ALL；其它类型为 null。
     */
    private String pickMode;

    private Date createTime;
}
