package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】MakerrewarddataDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 makerrewarddata，存储制作系统奖励数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makerrewarddata")
public class MakerrewarddataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer itemid;

    @Id
    private Integer rewardid;

    private Integer quantity;

    private Integer prob;

}
