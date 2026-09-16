package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
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
@Table("sys_admin_role")
public class SysAdminRoleDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String code;
    private String name;
    private String remark;
    private Integer enabled;
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date createdAt;
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date updatedAt;
}
