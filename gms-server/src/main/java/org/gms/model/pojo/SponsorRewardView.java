package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 赞助档位奖励视图（供 JS 脚本读取：getType / getId / getQty）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorRewardView {

    /** nx / maple / meso / item */
    private String type;

    /** 道具ID；非道具为 0 */
    private int id;

    /** 数量 */
    private int qty;
}
