package org.gms.model.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 命令请求参数
 * 支持命令的增删改查，包括等级、语法、类别和状态过滤
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandReqDTO extends BasePageDTO {
    /** 命令ID */
    private Integer id;
    /** 命令等级（增改用） */
    private Integer level;
    /** 命令等级列表（查询多选用） */
    private List<Integer> levelList;
    /** 语法（游戏中实际输入的指令） */
    private String syntax;
    /** 默认等级（增改用） */
    private Integer defaultLevel;
    /** 默认等级列表（查询用） */
    private List<Integer> defaultLevelList;
    /** 类名 */
    private String clazz;
    /** 描述（模糊查询用） */
    private String description;
    /** 是否启用（精确查询用） */
    private Boolean enabled;

}