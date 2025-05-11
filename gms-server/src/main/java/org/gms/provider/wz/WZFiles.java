package org.gms.provider.wz;

import lombok.extern.slf4j.Slf4j;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
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

    private final String fileName;
    public static final String DIRECTORY = "wz";

    WZFiles(String name) {
        this.fileName = name + ".wz";
    }

    public Path getFile() {
        // 优先取语言文件夹，没有则取wz
        ;
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        Path langPath = Path.of(DIRECTORY + "-" + serviceProperty.getLanguage(), fileName);
        boolean dirExists = Files.exists(langPath);
        log.info("wz 国际化[{}]目录是否存在:{}", langPath, dirExists);
        return dirExists ? langPath : Path.of(DIRECTORY, fileName);
    }

    public String getFilePath() {
        return getFile().toString();
    }
}
