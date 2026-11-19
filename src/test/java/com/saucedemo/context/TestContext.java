package com.saucedemo.context;

import com.microsoft.playwright.Page;
import com.saucedemo.config.PlaywrightManager;
import com.saucedemo.pages.*;

/** Shared per-scenario state, injected by PicoContainer. Page objects are created lazily. */
public class TestContext {
    private LoginPage login;
    private InventoryPage inventory;
    private CartPage cart;
    private CheckoutStepOnePage stepOne;
    private CheckoutStepTwoPage stepTwo;
    private CheckoutCompletePage complete;

    public String selectedProduct;

    public Page page() { return PlaywrightManager.page(); }

    public LoginPage loginPage() { return login == null ? login = new LoginPage(page()) : login; }
    public InventoryPage inventoryPage() { return inventory == null ? inventory = new InventoryPage(page()) : inventory; }
    public CartPage cartPage() { return cart == null ? cart = new CartPage(page()) : cart; }
    public CheckoutStepOnePage stepOnePage() { return stepOne == null ? stepOne = new CheckoutStepOnePage(page()) : stepOne; }
    public CheckoutStepTwoPage stepTwoPage() { return stepTwo == null ? stepTwo = new CheckoutStepTwoPage(page()) : stepTwo; }
    public CheckoutCompletePage completePage() { return complete == null ? complete = new CheckoutCompletePage(page()) : complete; }
}
