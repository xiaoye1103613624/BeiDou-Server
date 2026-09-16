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
@Table("sys_admin_role_menu")
public class SysAdminRoleMenuDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long roleId;
    @Id
    private Long menuId;
}
