package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class InventoryPage extends BasePage {
    public InventoryPage(Page page) { super(page); }

    public boolean isLoaded() {
        try {
            page.waitForURL("**/inventory.html");
            byTestId("inventory-container").waitFor();
            return true;
        } catch (com.microsoft.playwright.PlaywrightException e) {
            return false;
        }
    }

    public String title() { return text("title"); }

    private static String slug(String productName) {
        return productName.toLowerCase().replace(" ", "-");
    }

    public void addToCart(String productName) {
        click("add-to-cart-" + slug(productName));
    }

    public String cartBadgeCount() { return text("shopping-cart-badge"); }

    public void openCart() { click("shopping-cart-link"); waitForTitle("Your Cart"); }
}
