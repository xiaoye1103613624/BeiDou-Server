package org.gms.model.dto;

import lombok.Data;

/**
 * 文件树DTO
 * 用于返回文件树的根节点信息
 */
@Data
public class FileTreeDTO {
    /** 根节点标题 */
    private String title;
    /** 当前键 */
    private String currentKey;
}