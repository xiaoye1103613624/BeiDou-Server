package org.gms.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 赞助装备奖励自定义属性（字段对齐后台发放装备基础属性）。
 * custom 模式为绝对值：缺省/null → 0（覆盖 WZ 模板）。Boss%/无视等走潜能，不在此列。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SponsorEquipStats {

    private Short str;
    private Short dex;
    @JsonProperty("int")
    private Short _int;
    private Short luk;
    private Short hp;
    private Short mp;
    private Short pAtk;
    private Short mAtk;
    private Short pDef;
    private Short mDef;
    private Short acc;
    private Short avoid;
    private Short hands;
    private Short speed;
    private Short jump;
    private Byte upgradeSlot;
}
