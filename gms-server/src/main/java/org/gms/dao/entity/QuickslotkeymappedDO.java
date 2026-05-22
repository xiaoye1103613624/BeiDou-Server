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
 * 【实体】QuickslotkeymappedDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 quickslotkeymapped，存储快捷栏键位映射数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("quickslotkeymapped")
public class QuickslotkeymappedDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer accountid;

    private Long keymap;

}
