package com.saucedemo.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Generates a self-contained, Playwright-style HTML report (screenshots and video embedded as base64). */
public final class PlaywrightReportGenerator {

    public record Step(String keyword, String text, String status, long durationMs, String error) {}

    public static final class ScenarioResult {
        public String name, status, browser, error;
        public long durationMs;
        public List<String> tags = new ArrayList<>();
        public List<Step> steps = new ArrayList<>();
        public Path trace, video, screenshot;
    }

    private static final List<ScenarioResult> RESULTS = new CopyOnWriteArrayList<>();

    private PlaywrightReportGenerator() {}

    public static void add(ScenarioResult r) { RESULTS.add(r); }

    public static Path generate(Path reportDir) throws IOException {
        Files.createDirectories(reportDir);
        long passed = RESULTS.stream().filter(r -> "PASSED".equals(r.status)).count();
        long failed = RESULTS.size() - passed;
        long total = RESULTS.stream().mapToLong(r -> r.durationMs).sum();

        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Playwright Test Report</title><style>")
         .append("body{font-family:-apple-system,Segoe UI,Helvetica,Arial,sans-serif;margin:0;background:#f6f8fa;color:#1f2328}")
         .append("header{background:#fff;border-bottom:1px solid #d0d7de;padding:16px 32px}h1{margin:0;font-size:20px}")
         .append(".sum span{display:inline-block;margin-right:16px;font-size:14px}.ok{color:#1a7f37}.ko{color:#cf222e}")
         .append("main{max-width:1100px;margin:24px auto;padding:0 16px}")
         .append(".card{background:#fff;border:1px solid #d0d7de;border-radius:6px;margin-bottom:16px}")
         .append(".card>.hd{padding:12px 16px;border-bottom:1px solid #d0d7de;display:flex;justify-content:space-between;font-weight:600}")
         .append(".body{padding:12px 16px}.tag{background:#ddf4ff;color:#0969da;border-radius:12px;padding:1px 8px;font-size:12px;margin-left:6px}")
         .append("table{width:100%;border-collapse:collapse;font-size:14px}td{padding:6px 8px;border-bottom:1px solid #eaeef2}")
         .append("td.d{text-align:right;color:#656d76;width:90px}.err{background:#ffebe9;border:1px solid #ff818266;padding:8px;margin-top:8px;white-space:pre-wrap;font-family:monospace;font-size:12px}")
         .append("img,video{max-width:100%;border:1px solid #d0d7de;border-radius:6px;margin-top:8px}h3{font-size:14px;margin:16px 0 4px}")
         .append("code{background:#eaeef2;padding:2px 6px;border-radius:4px}</style></head><body>");
        h.append("<header><h1>🎭 Playwright Test Report</h1><div class='sum'>")
         .append("<span>Total: ").append(RESULTS.size()).append("</span>")
         .append("<span class='ok'>✓ Passed: ").append(passed).append("</span>")
         .append("<span class='ko'>✗ Failed: ").append(failed).append("</span>")
         .append("<span>Duration: ").append(fmt(total)).append("</span></div></header><main>");

        for (ScenarioResult r : RESULTS) {
            boolean ok = "PASSED".equals(r.status);
            h.append("<div class='card'><div class='hd'><div>").append(ok ? "<span class='ok'>✓</span> " : "<span class='ko'>✗</span> ")
             .append(esc(r.name));
            r.tags.forEach(t -> h.append("<span class='tag'>").append(esc(t)).append("</span>"));
            h.append("</div><div>").append(esc(r.browser)).append(" · ").append(fmt(r.durationMs)).append("</div></div><div class='body'>");
            h.append("<h3>Steps</h3><table>");
            for (Step s : r.steps) {
                boolean sok = "PASSED".equals(s.status);
                h.append("<tr><td>").append(sok ? "<span class='ok'>✓</span>" : "<span class='ko'>✗</span>").append("</td><td><b>")
                 .append(esc(s.keyword)).append("</b>").append(esc(s.text)).append("</td><td class='d'>")
                 .append(fmt(s.durationMs)).append("</td></tr>");
            }
            h.append("</table>");
            if (r.error != null) h.append("<div class='err'>").append(esc(r.error)).append("</div>");
            if (r.trace != null) {
                h.append("<h3>Trace</h3><a href='").append(esc(rel(reportDir, r.trace))).append("'>")
                 .append(esc(r.trace.getFileName().toString())).append("</a> — open with <code>./gradlew showTrace -PtraceFile=")
                 .append(esc(r.trace.toString())).append("</code> or drop it on <a href='https://trace.playwright.dev'>trace.playwright.dev</a>");
            }
            if (r.video != null && Files.exists(r.video)) {
                h.append("<h3>Video</h3><video controls src='data:video/webm;base64,").append(b64(r.video)).append("'></video>");
            }
            if (r.screenshot != null && Files.exists(r.screenshot)) {
                h.append("<h3>Final screenshot</h3><img src='data:image/png;base64,").append(b64(r.screenshot)).append("'/>");
            }
            h.append("</div></div>");
        }
        h.append("</main></body></html>");
        Path out = reportDir.resolve("index.html");
        Files.writeString(out, h.toString());
        return out;
    }

    private static String b64(Path p) {
        try { return Base64.getEncoder().encodeToString(Files.readAllBytes(p)); }
        catch (IOException e) { return ""; }
    }
    private static String rel(Path base, Path p) {
        return base.toAbsolutePath().relativize(p.toAbsolutePath()).toString().replace('\\', '/');
    }
    private static String fmt(long ms) {
        Duration d = Duration.ofMillis(ms);
        return d.toMinutes() > 0 ? d.toMinutes() + "m " + d.toSecondsPart() + "s" : String.format("%.1fs", ms / 1000.0);
    }
    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }
}
