package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_admin_account_role")
public class SysAdminAccountRoleDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer accountId;
    private Long roleId;
}
