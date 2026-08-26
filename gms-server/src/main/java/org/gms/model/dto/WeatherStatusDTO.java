package org.gms.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Web 天气管理页状态。与 {@code !weather} 报告字段对齐，不改指令逻辑。
 */
@Data
@Builder
public class WeatherStatusDTO {
    /** 当前游戏时刻，如 13:45 */
    private String clock;
    /** 未冻结时墙钟对应时刻 */
    private String wallClock;
    /** 0.0 白昼 ~ 1.0 深夜 */
    private float nightLevel;
    private boolean timeFrozen;
    private boolean skyForced;
    private boolean bareSky;
    private byte skyId;
    private String skyName;
    private String skyNameZh;
    /** 等价指令提示，如 !weather night rain */
    private String equivalentCommand;
    private List<Option> timeOptions;
    private List<Option> skyOptions;

    @Data
    @Builder
    public static class Option {
        private String value;
        private String label;
        private String commandToken;
    }
}
