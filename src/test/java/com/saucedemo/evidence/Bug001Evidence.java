package com.saucedemo.evidence;

import com.microsoft.playwright.*;
import com.saucedemo.config.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Reproduces BUG-001 and saves step screenshots to docs/bug-reports/BUG-001. Run: ./gradlew captureBug001 */
public class Bug001Evidence {
    public static void main(String[] args) throws Exception {
        Path out = Paths.get("docs", "bug-reports", "BUG-001");
        Files.createDirectories(out);
        try (Playwright pw = Playwright.create()) {
            pw.selectors().setTestIdAttribute("data-test");
            Browser browser = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(Config.headless()));
            Page page = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720)).newPage();
            page.setDefaultTimeout(15_000);

            page.navigate(Config.baseUrl());
            login(page, "standard_user");
            shot(page, out, "01-standard-user-inventory");
            page.getByTestId("add-to-cart-sauce-labs-backpack").click();
            shot(page, out, "02-standard-user-added-backpack");
            page.getByTestId("shopping-cart-link").click();
            page.waitForURL("**/cart.html"); page.getByTestId("cart-list").waitFor();
            shot(page, out, "03-standard-user-cart");

            page.getByText("Open Menu").click();
            page.getByTestId("logout-sidebar-link").click();
            page.getByTestId("login-button").waitFor();

            login(page, "problem_user");
            shot(page, out, "04-problem-user-inventory-badge-present");
            page.getByTestId("shopping-cart-link").click();
            page.waitForURL("**/cart.html"); page.getByTestId("cart-list").waitFor();
            shot(page, out, "05-problem-user-cart-contains-standard-user-item");

            String badge = page.getByTestId("shopping-cart-link").innerText().trim();
            String items = String.join(", ", page.getByTestId("inventory-item-name").allInnerTexts());
            System.out.println("problem_user cart badge=" + badge + " items=" + items);
            browser.close();
        }
    }

    private static void login(Page page, String user) {
        page.getByTestId("username").fill(user);
        page.getByTestId("password").fill("secret_sauce");
        page.getByTestId("login-button").click();
        page.getByTestId("inventory-container").waitFor();
    }

    private static void shot(Page page, Path dir, String name) {
        page.screenshot(new Page.ScreenshotOptions().setPath(dir.resolve(name + ".png")));
    }
}
