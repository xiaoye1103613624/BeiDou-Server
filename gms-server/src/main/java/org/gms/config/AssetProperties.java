package org.gms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Admin UI game-asset cache and remote icon providers.
 * <p>
 * Icons land only under {@code static/game-assets/{category}/{id}.png}.
 */
@Data
@Component
@ConfigurationProperties(prefix = "gms.assets")
public class AssetProperties {
    /** HTTP connect/read timeout for remote providers (seconds). */
    private int httpTimeoutSeconds = 15;

    private Cdn cdn = new Cdn();
    private Booklet booklet = new Booklet();

    @Data
    public static class Cdn {
        private boolean enabled = true;
        private String location = "GMS";
        private String version = "83";
    }

    @Data
    public static class Booklet {
        /**
         * Secondary icon source for custom / CN private-server IDs when maplestory.io misses.
         * Default uses a public item PNG CDN commonly mirrored by booklet-style sites.
         */
        private boolean enabled = true;
        /** Template with {@code {id}} and optional {@code {category}} placeholders. */
        private String iconUrlTemplate = "https://static.mapleartale.com/images/{category}/{id}.png";
    }
}
