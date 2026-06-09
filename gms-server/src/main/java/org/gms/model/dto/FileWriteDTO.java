package org.gms.model.dto;

import lombok.Data;

/**
 * 文件写入请求参数
 */
@Data
public class FileWriteDTO {
    /** 文件标题 */
    private String title;
    /** 文件路径键 */
    private String currentKey;
    /** 文件内容 */
    private String content;
}