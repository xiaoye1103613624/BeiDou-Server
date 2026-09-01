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
@Table("sys_admin_menu")
public class SysAdminMenuDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String localeKey;
    private String icon;
    private Integer sortOrder;
    /** 0目录 1菜单 2外链 */
    private Integer menuType;
    private String roles;
    private Integer requiresAuth;
    private Integer hideInMenu;
    private Integer enabled;
    private String remark;
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date createdAt;
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date updatedAt;
}
