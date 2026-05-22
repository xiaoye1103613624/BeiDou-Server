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
 * 【实体】FamilyCharacterDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 family_character，存储家族角色关联数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("family_character")
public class FamilyCharacterDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer cid;

    private Integer familyid;

    private Integer seniorid;

    private Integer reputation;

    private Integer todaysrep;

    private Integer totalreputation;

    private Integer reptosenior;

    private String precepts;

    private Long lastresettime;

}
