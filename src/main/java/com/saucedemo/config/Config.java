package com.saucedemo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Central configuration. Resolution order (highest first):
 * 1. JVM system property (-Dkey=value, key lower-case with dots e.g. base.url)
 * 2. OS environment variable (BASE_URL)
 * 3. .env file in the project root
 * 4. built-in default
 */
public final class Config {
    private static final Map<String, String> DOT_ENV = loadDotEnv(Paths.get(".env"));

    private Config() {}

    public static String get(String key, String defaultValue) {
        String prop = System.getProperty(key.toLowerCase().replace('_', '.'));
        if (prop != null && !prop.isBlank()) return prop;
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;
        String file = DOT_ENV.get(key);
        if (file != null && !file.isBlank()) return file;
        return defaultValue;
    }

    public static String baseUrl() { return get("BASE_URL", "https://www.saucedemo.com/"); }
    public static String browser() { return get("BROWSER", "chromium").toLowerCase(); }
    public static boolean headless() { return Boolean.parseBoolean(get("HEADLESS", "true")); }
    public static int timeoutMs() { return Integer.parseInt(get("DEFAULT_TIMEOUT_MS", "15000")); }

    private static Map<String, String> loadDotEnv(Path path) {
        Map<String, String> map = new HashMap<>();
        if (!Files.isRegularFile(path)) return map;
        try {
            for (String line : Files.readAllLines(path)) {
                String l = line.trim();
                if (l.isEmpty() || l.startsWith("#") || !l.contains("=")) continue;
                int i = l.indexOf('=');
                String v = l.substring(i + 1).trim();
                if (v.length() >= 2 && (v.startsWith("\"") && v.endsWith("\"") || v.startsWith("'") && v.endsWith("'"))) {
                    v = v.substring(1, v.length() - 1);
                }
                map.put(l.substring(0, i).trim(), v);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
        return map;
    }
}
