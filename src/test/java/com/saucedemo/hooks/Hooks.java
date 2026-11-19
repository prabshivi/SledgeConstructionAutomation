package com.saucedemo.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.saucedemo.config.PlaywrightManager;
import com.saucedemo.context.TestContext;
import com.saucedemo.reporting.PlaywrightReportGenerator;
import com.saucedemo.reporting.PlaywrightReportGenerator.ScenarioResult;
import io.cucumber.java.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Hooks {
    private final TestContext ctx;
    private long startNanos;

    public Hooks(TestContext ctx) { this.ctx = ctx; }

    @Before
    public void setUp(Scenario scenario) {
        startNanos = System.nanoTime();
        PlaywrightManager.start(); // browser + context(video, tracing) + page
    }

    @After
    public void tearDown(Scenario scenario) throws Exception {
        String safe = scenario.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path trace = PlaywrightManager.TRACE_DIR.resolve(safe + "-trace.zip");
        Path shot = PlaywrightManager.SCREENSHOT_DIR.resolve(safe + ".png");
        Files.createDirectories(trace.getParent());
        Files.createDirectories(shot.getParent());

        Page page = PlaywrightManager.page();
        Path video = null;
        try {
            byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(shot).setFullPage(true));
            scenario.attach(png, "image/png", "Final screenshot");
            video = page.video() != null ? page.video().path() : null;
        } catch (Exception ignored) { }

        PlaywrightManager.context().tracing().stop(new Tracing.StopOptions().setPath(trace));
        Files.copy(trace, trace.resolveSibling("latest-trace.zip"), StandardCopyOption.REPLACE_EXISTING);
        PlaywrightManager.stop(); // closes context -> finalizes video

        ScenarioResult r = new ScenarioResult();
        r.name = scenario.getName();
        r.status = scenario.getStatus().name();
        r.browser = PlaywrightManager.browserName() + (PlaywrightManager.headless() ? " (headless)" : "");
        r.durationMs = (System.nanoTime() - startNanos) / 1_000_000;
        r.tags.addAll(scenario.getSourceTagNames());
        r.trace = trace;
        r.video = video;
        r.screenshot = shot;
        r.steps.addAll(StepTimelinePlugin.stepsFor(scenario.getName()));
        if (scenario.isFailed()) {
            r.error = r.steps.stream().filter(s -> s.error() != null).map(s -> s.error()).findFirst().orElse("Scenario failed");
        }
        PlaywrightReportGenerator.add(r);
    }

    @AfterAll
    public static void buildReport() throws Exception {
        Path index = PlaywrightReportGenerator.generate(PlaywrightManager.REPORT_DIR);
        System.out.println("Playwright report: " + index.toAbsolutePath());
    }
}
