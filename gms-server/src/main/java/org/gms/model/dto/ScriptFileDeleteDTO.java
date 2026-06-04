package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本文件/目录删除请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptFileDeleteDTO {
    /** 要删除的相对路径 */
    private String path;
}
