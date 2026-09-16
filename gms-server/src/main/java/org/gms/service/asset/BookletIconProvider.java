package org.gms.service.asset;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AssetProperties;
import org.gms.server.icon.SharedIconFiles;
import org.gms.util.I18nUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Secondary remote provider for booklet / CN mirror icon PNGs when maplestory.io misses.
 * Only attempts categories that the template can express (typically {@code item}).
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class BookletIconProvider implements IconProvider {
    private final AssetProperties assetProperties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public String name() {
        return "booklet";
    }

    @Override
    public boolean enabled() {
        AssetProperties.Booklet booklet = assetProperties.getBooklet();
        return booklet != null && booklet.isEnabled()
                && booklet.getIconUrlTemplate() != null
                && !booklet.getIconUrlTemplate().isBlank();
    }

    public String buildUrl(String category, int objectId) {
        if (!enabled() || objectId <= 0) {
            return "";
        }
        String cat = SharedIconFiles.normalizeCategory(category);
        // Default mirror only hosts item icons; skip other categories unless template has no {category}.
        String template = assetProperties.getBooklet().getIconUrlTemplate().trim();
        if (template.contains("{category}") && !"item".equals(cat) && !"mob".equals(cat) && !"npc".equals(cat)) {
            return "";
        }
        return template
                .replace("{category}", cat)
                .replace("{id}", String.valueOf(objectId));
    }

    @Override
    public Optional<byte[]> fetchPng(String category, int objectId) {
        String url = buildUrl(category, objectId);
        if (url.isEmpty()) {
            return Optional.empty();
        }
        int timeout = Math.max(5, assetProperties.getHttpTimeoutSeconds());
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout))
                    .header("User-Agent", "BeiDou-Server-AssetCache/1.0")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                log.debug(I18nUtil.getLogMessage("Asset.booklet.http"), category, objectId, response.statusCode());
                return Optional.empty();
            }
            byte[] body = response.body();
            if (!SharedIconFiles.isPng(body)) {
                log.debug(I18nUtil.getLogMessage("Asset.booklet.notPng"), category, objectId);
                return Optional.empty();
            }
            return Optional.of(body);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(I18nUtil.getLogMessage("Asset.booklet.fail"), category, objectId, e.toString());
            return Optional.empty();
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("Asset.booklet.fail"), category, objectId, e.toString());
            return Optional.empty();
        }
    }
}
