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
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Primary remote provider: maplestory.io GMS icons.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class MapleStoryIoIconProvider implements IconProvider {
    private static final Set<String> CDN_CATEGORIES = Set.of(
            "item", "mob", "npc", "skill", "map", "quest"
    );

    private final AssetProperties assetProperties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public String name() {
        return "cdn";
    }

    @Override
    public boolean enabled() {
        return assetProperties.getCdn() == null || assetProperties.getCdn().isEnabled();
    }

    public String buildUrl(String category, int objectId) {
        if (!enabled() || objectId <= 0) {
            return "";
        }
        String cat = SharedIconFiles.normalizeCategory(category);
        if (!CDN_CATEGORIES.contains(cat)) {
            return "";
        }
        AssetProperties.Cdn cdn = assetProperties.getCdn();
        String location = cdn != null && cdn.getLocation() != null ? cdn.getLocation() : "GMS";
        String version = cdn != null && cdn.getVersion() != null ? cdn.getVersion() : "83";
        return String.format(Locale.ROOT,
                "https://maplestory.io/api/%s/%s/%s/%d/icon",
                location, version, cat, objectId);
    }

    @Override
    public Optional<byte[]> fetchPng(String category, int objectId) {
        String url = buildUrl(category, objectId);
        if (url.isEmpty()) {
            return Optional.empty();
        }
        return downloadPng(category, objectId, url);
    }

    private Optional<byte[]> downloadPng(String category, int objectId, String url) {
        int timeout = Math.max(5, assetProperties.getHttpTimeoutSeconds());
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout))
                    .header("User-Agent", "BeiDou-Server-AssetCache/1.0")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                log.debug(I18nUtil.getLogMessage("GameIcon.cdn.http"), category, objectId, response.statusCode());
                return Optional.empty();
            }
            byte[] body = response.body();
            if (!SharedIconFiles.isPng(body)) {
                log.debug(I18nUtil.getLogMessage("GameIcon.cdn.notPng"), category, objectId);
                return Optional.empty();
            }
            return Optional.of(body);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(I18nUtil.getLogMessage("GameIcon.cdn.fail"), category, objectId, e.toString());
            return Optional.empty();
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("GameIcon.cdn.fail"), category, objectId, e.toString());
            return Optional.empty();
        }
    }
}
