package org.gms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves local item icon PNGs for admin UI (custom items are missing on maplestory.io).
 */
@Configuration
public class ItemIconWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path iconDir = resolveIconDir();
        if (iconDir == null) {
            return;
        }
        String location = iconDir.toAbsolutePath().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/item-icons/**")
                .addResourceLocations(location);
    }

    private static Path resolveIconDir() {
        String[] candidates = {
                "tools/_full_icon_sync/web_png",
                "gms-server/tools/_full_icon_sync/web_png",
                System.getProperty("user.dir") + "/tools/_full_icon_sync/web_png",
                "E:/pro/BeiDou-Server_xy/gms-server/tools/_full_icon_sync/web_png"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.isDirectory(path)) {
                return path;
            }
        }
        return null;
    }
}
