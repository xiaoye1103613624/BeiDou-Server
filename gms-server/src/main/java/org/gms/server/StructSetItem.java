package org.gms.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 套装属性配置（对应Etc.wz/SetItemInfo.img中的单个套装节点）
 * completeCount为套装总件数，items为达到指定穿戴件数(tier)时叠加生效的属性加成
 */
public class StructSetItem {
    /** 套装ID */
    public int setItemID;
    /** 套装总件数 */
    public int completeCount;
    /** 套装包含的所有装备ID */
    public final List<Integer> itemIDs = new ArrayList<>();
    /** 穿戴件数(tier) -> 该档位生效的属性加成，按WZ顺序保留 */
    public final Map<Integer, SetItem> items = new LinkedHashMap<>();

    public Map<Integer, SetItem> getItems() {
        return new LinkedHashMap<>(items);
    }

    /** 单个穿戴档位生效的属性加成 */
    public static class SetItem {
        public int incPDD;
        public int incMDD;
        public int incSTR;
        public int incDEX;
        public int incINT;
        public int incLUK;
        public int incACC;
        public int incEVA;
        public int incPAD;
        public int incMAD;
        public int incSpeed;
        public int incMHP;
        public int incMMP;
        public int incMHPr;
        public int incMMPr;
        public int incAllStat;
        /** 潜能选项1（对应ItemOptionInfo.img的option编号）及其等级 */
        public int option1;
        public int option1Level;
        /** 潜能选项2及其等级 */
        public int option2;
        public int option2Level;
    }
}
