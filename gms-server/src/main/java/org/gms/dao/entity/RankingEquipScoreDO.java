package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ranking_equip_score")
public class RankingEquipScoreDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long inventoryItemId;
    private Integer characterId;
    private String characterName;
    private Integer world;
    private Integer itemId;
    private String itemName;
    private Short position;
    private Integer slotCategory;
    private Integer equipped;
    private Long score;
    private Short str;
    private Short dex;
    @Column("inte")
    private Short inte;
    private Short luk;
    private Short hp;
    private Short mp;
    private Short watk;
    private Short matk;
    private Short wdef;
    private Short mdef;
    private Short acc;
    private Short avoid;
    private Short hands;
    private Short speed;
    private Short jump;
    private Byte upgradeslots;
    private Byte level;
    private Short vicious;
    private Byte itemlevel;
    private Integer itemexp;
    private Timestamp updatedAt;
}
