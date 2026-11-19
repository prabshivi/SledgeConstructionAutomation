package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected Locator byTestId(String id) {
        return page.getByTestId(id);
    }

    protected void click(String testId) {
        byTestId(testId).click();
    }

    protected void fill(String testId, String value) {
        byTestId(testId).fill(value);
    }

    protected String text(String testId) {
        return byTestId(testId).innerText().trim();
    }

    protected void waitForTitle(String expected) {
        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(byTestId("title")).hasText(expected);
    }

    public String currentUrl() {
        return page.url();
    }
}
