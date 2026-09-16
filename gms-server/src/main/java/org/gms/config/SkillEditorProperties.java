package org.gms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 技能 Web 编辑器开发开关（生产默认关闭）。
 * <p>
 * 也可通过 game_config {@code allow_skill_editor_write=true} 热开（OR 关系）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "gms.dev.skill-editor")
public class SkillEditorProperties {
    /** 允许写服务端 Skill/String XML 与 reloadSkills */
    private boolean enabled = false;
}
