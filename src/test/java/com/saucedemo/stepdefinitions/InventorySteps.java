package com.saucedemo.stepdefinitions;

import com.saucedemo.context.TestContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class InventorySteps {
    private final TestContext ctx;
    public InventorySteps(TestContext ctx) { this.ctx = ctx; }

    @Then("the products page is displayed")
    public void productsDisplayed() {
        assertTrue(ctx.inventoryPage().isLoaded(), "Inventory page should be loaded");
        assertEquals("Products", ctx.inventoryPage().title());
    }

    @When("the user adds {string} to the cart")
    public void addToCart(String product) {
        ctx.selectedProduct = product;
        ctx.inventoryPage().addToCart(product);
    }

    @Then("the cart badge shows {int} item(s)")
    public void badge(int count) {
        assertEquals(String.valueOf(count), ctx.inventoryPage().cartBadgeCount());
    }

    @When("the user opens the cart")
    public void openCart() { ctx.inventoryPage().openCart(); }
}
