package org.gms.model.dto;

import lombok.Data;

/**
 * Web 天气切换请求。语义对齐 {@code !weather}：
 * <ul>
 *   <li>{@code auto=true}：等价 {@code !weather auto}（时段与天空一并交还）</li>
 *   <li>否则 {@code time} / {@code sky} 可单独或同时设置（组合）；各自选项组内互斥</li>
 * </ul>
 */
@Data
public class WeatherApplyDTO {
    /** 立即解除全部覆盖 */
    private boolean auto;

    /**
     * 时段：null / keep=不改；day|dusk|night|dawn|midnight|clock|release
     */
    private String time;

    /** time=clock 时使用，支持 HH:MM / HHMM / H */
    private String clock;

    /**
     * 天空：null / keep=不改；clear|rain|snow|overcast|storm|blizzard|leaves|blossom|sandstorm|release
     */
    private String sky;

    /** true=瞬间切换；false=淡入（与指令广播一致，默认 false） */
    private Boolean snap;
}
