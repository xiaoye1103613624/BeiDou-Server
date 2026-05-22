package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】QueststatusDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 queststatus，存储任务状态数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("queststatus")
public class QueststatusDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long queststatusid;

    private Integer characterid;

    private Integer quest;

    private Integer status;

    private Integer time;

    private Long expires;

    private Integer forfeited;

    private Integer completed;

    private Integer info;

}
