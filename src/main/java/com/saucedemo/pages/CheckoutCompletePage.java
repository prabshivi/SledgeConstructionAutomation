package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class CheckoutCompletePage extends BasePage {
    public CheckoutCompletePage(Page page) { super(page); }

    public String title() { return text("title"); }
    public String header() { return text("complete-header"); }
    public String body() { return text("complete-text"); }

    public void backHome() { click("back-to-products"); }
}
