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

/**
 * CDK道具奖励子表实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_cdk_item")
public class CdkItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联CDK配置ID */
    private Long cdkId;

    /** 道具ID */
    private Integer itemId;

    /** 发放数量 */
    private Integer quantity;
}
