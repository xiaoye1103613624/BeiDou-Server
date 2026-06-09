package org.gms.model.pojo;

import lombok.Data;
import org.gms.constants.game.NextLevelType;

/**
 * 下一等级上下文
 * 记录等级过渡信息，用于技能升级等场景
 */
@Data
public class NextLevelContext {
    /** 等级类型 */
    private NextLevelType levelType;
    /** 上一等级 */
    private String lastLevel;
    /** 下一等级 */
    private String nextLevel;
    /** 前缀 */
    private String prefix;

    /**
     * 清空所有上下文信息
     */
    public void clear() {
        this.levelType = null;
        this.lastLevel = null;
        this.nextLevel = null;
        this.prefix = null;
    }
}