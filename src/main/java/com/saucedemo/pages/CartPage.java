package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class CartPage extends BasePage {
    public CartPage(Page page) { super(page); }

    public String title() { return text("title"); }

    public boolean hasItem(String productName) {
        return page.getByTestId("inventory-item-name").filter(
                new com.microsoft.playwright.Locator.FilterOptions().setHasText(productName)).count() > 0;
    }

    public int itemCount() { return page.getByTestId("inventory-item").count(); }

    public void checkout() { click("checkout"); waitForTitle("Checkout: Your Information"); }
}
