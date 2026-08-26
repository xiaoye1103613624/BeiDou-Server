package org.gms.config;

import org.gms.server.icon.SharedIconFiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves filesystem icon cache at {@code /icons/{type}/{id}.png}.
 * Prefer {@code GET /icon/v1/{type}/{id}} for lazy fill; this is a static fallback.
 */
@Configuration
public class SharedIconWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var root = SharedIconFiles.resolveOrCreateRoot();
        if (root == null) {
            return;
        }
        String location = root.toAbsolutePath().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/icons/**")
                .addResourceLocations(location);
    }
}
