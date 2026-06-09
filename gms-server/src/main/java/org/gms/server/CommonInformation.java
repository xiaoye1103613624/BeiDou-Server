package org.gms.server;

import org.gms.constants.api.InformationType;
import org.gms.exception.BizException;
import org.gms.model.pojo.InformationSearch;
import org.gms.model.pojo.InformationResult;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用信息查询
 * 从WZ String数据中查询物品、装备、怪物、NPC、地图等通用信息，支持模糊/精确匹配
 * 使用单例模式
 */
public class CommonInformation {
    private static CommonInformation instance;
    /** String.wz数据源 */
    private final DataProvider stringData;

    private CommonInformation() {
        stringData = DataProviderFactory.getDataProvider(WZFiles.STRING);
    }

    public static CommonInformation getInstance() {
        if (instance == null) {
            instance = new CommonInformation();
        }
        return instance;
    }

    /**
     * 硬查xml
     * 因为支持模糊匹配，所以无法使用lru缓存，否则可能导致查出的数据不完整
     */
    public List<InformationResult> getStringInformation(InformationSearch condition) {
        RequireUtil.requireNotEmpty(condition.getTypes(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "types"));
        List<InformationResult> results = new ArrayList<>();
        for (String type : condition.getTypes()) {
            InformationType infType = InformationType.ofType(type);
            if (infType == null) {
                throw new BizException(I18nUtil.getExceptionMessage("UNSUPPORTED_TYPE"));
            }
            searchXML(results, infType, condition.getFilter(), condition.getFilterType(), condition.isFullMatch());
        }
        return results;
    }

    private void searchXML(List<InformationResult> results, InformationType infType, String filter, int filterType, boolean fullMatch) {
        Data data;
        switch (infType) {
            case CASH -> {
                data = stringData.getData("Cash.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case CONSUME -> {
                data = stringData.getData("Consume.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case EQP -> {
                data = stringData.getData("Eqp.img").getChildByPath("Eqp");
                for (Data child : data.getChildren()) {
                    addResult(results, infType, child, filter, filterType, fullMatch);
                }
            }
            case ETC -> {
                data = stringData.getData("Etc.img").getChildByPath("Etc");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case INS -> {
                data = stringData.getData("Ins.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case MAP -> {
                data = stringData.getData("Map.img");
                for (Data child : data.getChildren()) {
                    addMapResult(results, infType, child, filter, filterType, fullMatch);
                }
            }
            case MOB -> {
                data = stringData.getData("Mob.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case NPC -> {
                data = stringData.getData("Npc.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case PET -> {
                data = stringData.getData("Pet.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
            case SKILL -> {
                data = stringData.getData("Skill.img");
                addResult(results, infType, data, filter, filterType, fullMatch);
            }
        }
    }

    private void addResult(List<InformationResult> results, InformationType infType, Data data, String filter, int filterType, boolean fullMatch) {
        RequireUtil.requireNotNull(data, I18nUtil.getExceptionMessage("MISSING_RESOURCE", infType.getType()));
        for (Data child : data.getChildren()) {
            String id = child.getName();
            String name = DataTool.getString("name", child, "");
            String desc = DataTool.getString("desc", child, "");
            if (isMatch(id, name, filter, filterType, fullMatch)) {
                results.add(InformationResult.builder()
                        .type(infType.getType())
                        .id(Integer.parseInt(id))
                        .name(name)
                        .desc(desc)
                        .build());
            }
        }
    }

    /**
     * 将匹配的地图子项添加到结果列表（地图使用mapName/streetName而非name/desc）
     *
     * @param results   结果列表
     * @param infType   信息类型
     * @param data      数据节点
     * @param filter    过滤关键字
     * @param filterType 过滤类型
     * @param fullMatch 是否完全匹配
     */
    private void addMapResult(List<InformationResult> results, InformationType infType, Data data, String filter, int filterType, boolean fullMatch) {
        RequireUtil.requireNotNull(data, I18nUtil.getExceptionMessage("MISSING_RESOURCE", infType.getType()));
        for (Data child : data.getChildren()) {
            String id = child.getName();
            String name = DataTool.getString("mapName", child, "");
            String desc = DataTool.getString("streetName", child, "");
            if (isMatch(id, name, filter, filterType, fullMatch)) {
                results.add(InformationResult.builder()
                        .type(infType.getType())
                        .id(Integer.parseInt(id))
                        .name(name)
                        .desc(desc)
                        .build());
            }
        }
    }

    /**
     * 判断id或name是否匹配过滤条件
     *
     * @param id         物品ID字符串
     * @param name       物品名称
     * @param filter     过滤关键字
     * @param filterType 过滤类型
     * @param fullMatch  是否完全匹配
     * @return 匹配返回true
     */
    private boolean isMatch(String id, String name, String filter, int filterType, boolean fullMatch) {
        boolean match = false;
        if (filterType == 0 || filterType == 1) {
            match = fullMatch ? id.equals(filter) : id.contains(filter);
        }
        if (match) {
            return true;
        }
        if (filterType == 0 || filterType == 2) {
            match = fullMatch ? name.equals(filter) : name.contains(filter);
        }
        return match;
    }
}