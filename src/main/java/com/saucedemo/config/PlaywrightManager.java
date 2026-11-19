package com.saucedemo.config;

import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Thread-safe holder for Playwright, Browser, BrowserContext (tracing + video) and Page. */
public final class PlaywrightManager {

    public static final Path REPORT_DIR = Paths.get("build", "reports", "playwright");
    public static final Path VIDEO_DIR = REPORT_DIR.resolve("videos");
    public static final Path TRACE_DIR = REPORT_DIR.resolve("traces");
    public static final Path SCREENSHOT_DIR = REPORT_DIR.resolve("screenshots");

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {}

    public static String browserName() {
        return Config.browser();
    }

    public static boolean headless() {
        return Config.headless();
    }

    /** Launches browser, creates context with video + tracing, and opens a page. */
    public static Page start() {
        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);
        playwright.selectors().setTestIdAttribute("data-test"); // saucedemo uses data-test

        BrowserType type;
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless());
        switch (browserName()) {
            case "firefox" -> type = playwright.firefox();
            case "webkit" -> type = playwright.webkit();
            case "chrome" -> { type = playwright.chromium(); options.setChannel("chrome"); }
            case "edge" -> { type = playwright.chromium(); options.setChannel("msedge"); }
            case "chromium" -> type = playwright.chromium();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName());
        }
        // Chromium-only flag to avoid password-manager/breach popups on saucedemo
        if (type == playwright.chromium()) {
            options.setArgs(List.of("--disable-features=PasswordLeakDetection,PasswordManagerOnboarding",
                    "--disable-save-password-bubble"));
        }
        Browser browser = type.launch(options);
        BROWSER.set(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setRecordVideoDir(VIDEO_DIR)
                .setRecordVideoSize(1280, 720));
        context.setDefaultTimeout(Config.timeoutMs());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true).setSnapshots(true).setSources(true));
        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);
        return page;
    }

    public static Page page() {
        return PAGE.get();
    }

    public static BrowserContext context() {
        return CONTEXT.get();
    }

    /** Closes everything and clears thread-locals. Video is finalized on context close. */
    public static void stop() {
        try { if (CONTEXT.get() != null) CONTEXT.get().close(); } catch (Exception ignored) { }
        try { if (BROWSER.get() != null) BROWSER.get().close(); } catch (Exception ignored) { }
        try { if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close(); } catch (Exception ignored) { }
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}
