package org.gms.model.dto;

import lombok.Data;

/**
 * 文件树请求DTO
 * <p>用于指定文件树操作的节点信息</p>
 */
@Data
public class FileTreeDTO {
    /** 节点显示标题 */
    private String title;
    /** 节点标识键 */
    private String currentKey;
}
