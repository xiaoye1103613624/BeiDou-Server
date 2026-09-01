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
import java.time.LocalDateTime;

/**
 * 游戏内右边栏 ServerTool 配置（脚本 + tip）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sidebar_tool_config")
public class SidebarToolConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private Integer toolIndex;

    private String label;

    private String scriptPath;

    private String tipTitle;

    private String tipDesc;

    private Integer enabled;

    private LocalDateTime updatedAt;
}
