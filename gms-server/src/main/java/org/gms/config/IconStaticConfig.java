package org.gms.config;

import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.icon.SharedIconFiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 将本地道具图标目录映射到管理后台可访问的静态路径。
 * <ul>
 *   <li>{@code /item-icons/**} → {@link ItemIconFiles} 缓存目录</li>
 *   <li>{@code /icons/**} → {@link SharedIconFiles} 共用缓存</li>
 * </ul>
 */
@Configuration
public class IconStaticConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path itemIconDir = ItemIconFiles.resolveOrCreateIconDir();
        registry.addResourceHandler("/item-icons/**")
                .addResourceLocations(itemIconDir.toUri().toString())
                .setCachePeriod(3600);

        Path sharedRoot = SharedIconFiles.resolveOrCreateRoot();
        registry.addResourceHandler("/icons/**")
                .addResourceLocations(sharedRoot.toUri().toString())
                .setCachePeriod(3600);
    }
}
