package org.gms.provider.wz;

import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * WZ文件枚举
 * 定义游戏客户端使用的WZ数据文件名称和路径
 */
public enum WZFiles {
    QUEST("Quest"),
    ETC("Etc"),
    ITEM("Item"),
    CHARACTER("Character"),
    STRING("String"),
    LIST("List"),
    MOB("Mob"),
    MAP("Map"),
    NPC("Npc"),
    REACTOR("Reactor"),
    SKILL("Skill"),
    SOUND("Sound"),
    UI("UI");

    private static final Logger log = LoggerFactory.getLogger(WZFiles.class);
    private final String fileName;
    public static final String DIRECTORY = "wz";

    WZFiles(String name) {
        this.fileName = name + ".wz";
    }

    public Path getFile() {
        // 优先取语言文件夹，没有则取wz
        Path wzPath = Path.of(DIRECTORY, fileName);
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        Path langPath = Path.of(DIRECTORY + "-" + serviceProperty.getLanguage(), fileName);

        if (Files.exists(langPath)) {
            return langPath;
        }
        if (Files.exists(wzPath)) {
            return wzPath;
        }

        // 兼容从项目根目录启动（如IDEA默认配置）：尝试 gms-server/ 子目录
        Path gmsWzPath = Path.of("gms-server", DIRECTORY, fileName);
        Path gmsLangPath = Path.of("gms-server", DIRECTORY + "-" + serviceProperty.getLanguage(), fileName);
        if (Files.exists(gmsLangPath)) {
            return gmsLangPath;
        }
        if (Files.exists(gmsWzPath)) {
            return gmsWzPath;
        }

        // 所有路径都不存在时，记录详细错误，帮助诊断工作目录问题
        log.error("WZ目录不存在！语言路径：{}，默认路径：{}，gms-server子目录路径：{}，当前工作目录：{}。请确认服务器从 gms-server/ 目录启动。",
                langPath.toAbsolutePath(), wzPath.toAbsolutePath(), gmsWzPath.toAbsolutePath(), System.getProperty("user.dir"));
        return wzPath;
    }

    public String getFilePath() {
        return getFile().toString();
    }
}