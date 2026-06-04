package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本文件/目录创建请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptFileCreateDTO {
    /** 要创建的相对路径 */
    private String path;
    /** 是否创建目录（false=创建文件） */
    private boolean directory;
}
