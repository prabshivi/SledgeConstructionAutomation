package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class CheckoutStepTwoPage extends BasePage {
    public CheckoutStepTwoPage(Page page) { super(page); }

    public String title() { return text("title"); }

    public boolean hasItem(String productName) {
        return page.getByTestId("inventory-item-name").filter(
                new com.microsoft.playwright.Locator.FilterOptions().setHasText(productName)).count() > 0;
    }

    public String total() { return text("total-label"); }

    public void finish() { click("finish"); waitForTitle("Checkout: Complete!"); }
}
