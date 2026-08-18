package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 赞助档位奖励视图（供 JS 脚本读取）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorRewardView {

    /** nx / maple / meso / item / skill_group */
    private String type;

    /** 奖励行主键（skill_group 选技时回传用）；非技能组也可有值 */
    private int rewardId;

    /** 道具ID；非道具为 0 */
    private int id;

    /** 数量；skill_group+MULTI 为需选个数 */
    private int qty;

    /** 是否为装备（item 且 ID 落在装备区间） */
    private boolean equip;

    /** default / custom；非装备为空串 */
    private String statMode;

    /** 玩家实际将获得的装备属性（模板解析或自定义）；非装备为 null */
    private SponsorEquipStats stats;

    /** 中文可读属性摘要，如「力量+10 物攻+50」；非装备或无属性为空串 */
    private String statsText;

    /** skill_group：ONE / MULTI / ALL；其它为空串 */
    private String pickMode;

    @Builder.Default
    private List<SponsorSkillOptionView> skillOptions = new ArrayList<>();
}
