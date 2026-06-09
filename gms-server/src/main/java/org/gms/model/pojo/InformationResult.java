package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息搜索结果
 * 封装游戏内信息搜索的返回结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResult {
    /** 类型 */
    private String type;
    /** ID */
    private Integer id;
    /** 名称 */
    private String name;
    /** 描述 */
    private String desc;
}