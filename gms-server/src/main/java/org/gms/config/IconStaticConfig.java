package org.gms.config;

import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.icon.SharedIconFiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Maps local icon directories to admin-UI static paths.
 * <ul>
 *   <li>{@code /game-assets/**} → unified {@link SharedIconFiles} cache</li>
 *   <li>{@code /icons/**} → same root (legacy alias)</li>
 *   <li>{@code /item-icons/**} → {@link ItemIconFiles} item-only cache</li>
 * </ul>
 */
@Configuration
public class IconStaticConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path sharedRoot = SharedIconFiles.resolveOrCreateRoot();
        String sharedLocation = sharedRoot.toUri().toString();

        registry.addResourceHandler("/game-assets/**")
                .addResourceLocations(sharedLocation)
                .setCachePeriod(3600);

        registry.addResourceHandler("/icons/**")
                .addResourceLocations(sharedLocation)
                .setCachePeriod(3600);

        Path itemIconDir = ItemIconFiles.resolveOrCreateIconDir();
        registry.addResourceHandler("/item-icons/**")
                .addResourceLocations(itemIconDir.toUri().toString())
                .setCachePeriod(3600);
    }
}
