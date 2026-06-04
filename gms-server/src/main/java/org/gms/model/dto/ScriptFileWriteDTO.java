package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本文件写入请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptFileWriteDTO {
    /** 文件相对路径 */
    private String path;
    /** 文件内容 */
    private String content;
}
