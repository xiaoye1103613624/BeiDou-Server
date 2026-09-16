package org.gms.service.asset;

import java.util.Optional;

/**
 * Remote (or secondary) source that can supply a PNG icon for admin UI cache.
 */
public interface IconProvider {
    /** Stable id for logs / API info (e.g. {@code cdn}, {@code booklet}). */
    String name();

    boolean enabled();

    /**
     * Download PNG bytes for the given category+id, or empty if unavailable.
     */
    Optional<byte[]> fetchPng(String category, int objectId);
}
