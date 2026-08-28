package org.gms.server.setitem;

import lombok.Getter;

@Getter
public enum SetBonusColor {
    DEFAULT("#k", "默认"),
    ENHANCE("#b", "蓝色"),
    SET_BONUS("#g", "绿色"),
    WARNING("#r", "红色"),
    EPIC("#d", "紫色"),
    LEGEND("#e", "黄色"),
    INFO("#c", "青色"),
    MUTED("#h", "灰色");

    private final String code;
    private final String label;

    SetBonusColor(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
