package org.gms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 座椅/骑宠姿态编辑器开发开关（生产默认关闭）。
 * <p>
 * 也可通过 game_config {@code allow_chair_editor_write=true} 热开（OR 关系）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "gms.dev.chair-editor")
public class ChairEditorProperties {
    /** 允许写服务端 Install / Character.TamingMob XML */
    private boolean enabled = false;
}
