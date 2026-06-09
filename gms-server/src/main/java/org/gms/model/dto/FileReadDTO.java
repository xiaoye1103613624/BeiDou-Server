package org.gms.model.dto;

import lombok.Data;

/**
 * 文件读取请求参数
 */
@Data
public class FileReadDTO {
    /** 文件标题 */
    private String title;
    /** 文件路径键 */
    private String currentKey;
}