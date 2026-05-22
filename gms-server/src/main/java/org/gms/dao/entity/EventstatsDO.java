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
 * 【实体】EventstatsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 eventstats，存储活动统计数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("eventstats")
public class EventstatsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long characterid;

    /**
     * 0
     */
    private String name;

    private Integer info;

}
