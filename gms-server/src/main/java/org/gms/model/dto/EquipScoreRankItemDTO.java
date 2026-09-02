package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipScoreRankItemDTO {
    private Integer rank;
    private Long inventoryItemId;
    private Integer characterId;
    private String characterName;
    private Integer world;
    private Integer itemId;
    private String itemName;
    private Short position;
    private Integer slotCategory;
    private String slotCategoryName;
    private Boolean equipped;
    private Long score;
    /** 悬浮属性 */
    private Short attStr;
    private Short attDex;
    private Short attInt;
    private Short attLuk;
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
    private Byte upgradeSlots;
    private Byte level;
    private Short vicious;
    private Byte itemLevel;
    private Integer itemExp;
}
