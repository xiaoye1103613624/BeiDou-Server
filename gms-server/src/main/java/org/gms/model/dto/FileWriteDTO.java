package org.gms.model.dto;

import lombok.Data;

/**
 * 文件写入请求DTO
 * <p>用于提交文件内容的写入操作</p>
 */
@Data
public class FileWriteDTO {
    /** 文件显示标题 */
    private String title;
    /** 文件标识键 */
    private String currentKey;
    /** 文件内容 */
    private String content;
}
