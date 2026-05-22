package org.gms.model.dto;

import lombok.Data;

/**
 * 文件读取请求DTO
 * <p>用于指定要读取的文件信息</p>
 */
@Data
public class FileReadDTO {
    /** 文件显示标题 */
    private String title;
    /** 文件标识键 */
    private String currentKey;
}
