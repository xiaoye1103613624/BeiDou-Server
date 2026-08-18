package org.gms.config;

import org.gms.server.cashshop.ItemIconFiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves local item icon PNGs for admin UI (custom items are missing on maplestory.io).
 */
@Configuration
public class ItemIconWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var iconDir = ItemIconFiles.resolveOrCreateIconDir();
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
}
