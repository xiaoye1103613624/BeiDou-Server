package org.gms.model.dto;

import lombok.Data;

/**
 * 文件树变更请求：新建 / 重命名 / 复制 / 移动 / 删除。
 */
@Data
public class FileMutateDTO {
    /** 源节点树 key；新建时表示父目录 key（根目录可为空）。 */
    private String currentKey;
    /** 目标父目录树 key（复制 / 移动）。 */
    private String targetParentKey;
    /** 新名称（新建 / 重命名 / 复制可选改名）。 */
    private String name;
    /** 新建时是否为目录。 */
    private Boolean directory;
}
