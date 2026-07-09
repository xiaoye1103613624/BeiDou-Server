package org.gms.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resolves server content directories (wz, scripts, handbook, etc.) when the JVM
 * starts from either gms-server/ or the repository root.
 */
public final class ContentRoot {
    private static final Logger log = LoggerFactory.getLogger(ContentRoot.class);
    private static volatile Path root;

    private ContentRoot() {
    }

    public static Path get() {
        if (root == null) {
            synchronized (ContentRoot.class) {
                if (root == null) {
                    root = resolveRoot();
                }
            }
        }
        return root;
    }

    public static Path resolve(String first, String... more) {
        return get().resolve(Path.of(first, more));
    }

    private static Path resolveRoot() {
        String configured = System.getProperty("gms.content.root");
        if (configured == null || configured.isBlank()) {
            configured = System.getProperty("gms.wz.root");
        }
        if (configured != null && !configured.isBlank()) {
            Path configuredPath = Path.of(configured).toAbsolutePath().normalize();
            if (isValidContentRoot(configuredPath)) {
                log.info("Using configured content root: {}", configuredPath);
                return configuredPath;
            }
            log.warn("Configured content root is missing wz/scripts: {}", configuredPath);
        }

        Path cwd = Path.of("").toAbsolutePath().normalize();
        if (isValidContentRoot(cwd)) {
            return cwd;
        }

        Path nested = cwd.resolve("gms-server");
        if (isValidContentRoot(nested)) {
            log.info("Using nested content root: {}", nested);
            return nested;
        }

        log.warn("Content root not found from cwd ({}) or {}/gms-server; falling back to cwd", cwd, cwd);
        return cwd;
    }

    private static boolean isValidContentRoot(Path candidate) {
        return Files.isDirectory(candidate.resolve("wz"))
                || Files.isDirectory(candidate.resolve("scripts"))
                || Files.isDirectory(candidate.resolve("scripts-zh-CN"));
    }
}