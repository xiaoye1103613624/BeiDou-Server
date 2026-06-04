package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本文件读取请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptFileReadDTO {
    /** 文件/目录相对路径 */
    private String path;
}
