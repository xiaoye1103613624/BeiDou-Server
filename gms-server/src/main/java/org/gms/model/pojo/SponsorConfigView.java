package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 赞助档位配置视图（供 JS：getId / getName / getAmount / getRewards）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorConfigView {

    private int id;
    private String name;
    private int amount;

    @Builder.Default
    private List<SponsorRewardView> rewards = new ArrayList<>();
}
