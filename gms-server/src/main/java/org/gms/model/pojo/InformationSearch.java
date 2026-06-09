package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 信息搜索条件
 * 用于游戏内各种信息的模糊搜索，支持按类型和ID/名称过滤
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationSearch {
    /** 搜索类型列表，对应InformationType枚举 */
    private List<String> types;
    /** 搜索过滤关键字，匹配ID或名称 */
    private String filter;
    /** 过滤类型：0=ID和名称都匹配，1=仅匹配ID，2=仅匹配名称 */
    private int filterType;
    /** 是否精确匹配 */
    private boolean fullMatch;
}