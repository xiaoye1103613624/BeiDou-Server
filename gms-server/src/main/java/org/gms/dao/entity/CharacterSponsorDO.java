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
 * 角色赞助余额：总赞助（档位）+ 可消费赞助（扣减）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_character_sponsor")
public class CharacterSponsorDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 总赞助（累计，只增不减） */
    private Integer totalSponsor;

    /** 可消费赞助（购买扣减） */
    private Integer spendableSponsor;

    private Date createTime;
    private Date updateTime;
}
