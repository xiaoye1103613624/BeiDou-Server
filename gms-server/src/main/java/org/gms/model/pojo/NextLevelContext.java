package org.gms.model.pojo;

import lombok.Data;
import org.gms.constants.game.NextLevelType;

/**
 * nextLevel上下文
 */
@Data
public class NextLevelContext {
    /**
     * 当前
     */
    private NextLevelType levelType;
    /**
     * 最后一级
     */
    private String lastLevel;
    /**
     * 下一级
     */
    private String nextLevel;
    /**
     * 上一级
     */
    private String prefix;

    public void clear() {
        this.levelType = null;
        this.lastLevel = null;
        this.nextLevel = null;
        this.prefix = null;
    }
}
