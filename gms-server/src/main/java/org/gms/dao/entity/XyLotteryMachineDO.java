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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_lottery_machine")
public class XyLotteryMachineDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Integer npcId;
    private String name;
    private String comment;
    private Integer enabled;
    /** JSON array e.g. [1,10,20] */
    private String multiDraws;
    private String costType;
    private Integer costItemId;
    private Long costAmount;
    private Date updatedAt;
}
