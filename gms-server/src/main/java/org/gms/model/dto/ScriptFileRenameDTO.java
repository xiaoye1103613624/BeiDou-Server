package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本文件重命名请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptFileRenameDTO {
    /** 原相对路径 */
    private String oldPath;
    /** 新相对路径 */
    private String newPath;
}
