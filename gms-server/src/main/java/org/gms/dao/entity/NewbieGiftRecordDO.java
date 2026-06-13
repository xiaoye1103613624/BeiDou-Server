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
 * 新手礼包领取记录实体（每角色每礼包唯一）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_newbie_gift_record")
public class NewbieGiftRecordDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 关联 xy_newbie_gift_config.id */
    private Long giftId;

    private Date claimTime;
}
