package com.saucedemo.stepdefinitions;

import com.saucedemo.context.TestContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class CartSteps {
    private final TestContext ctx;
    public CartSteps(TestContext ctx) { this.ctx = ctx; }

    @Then("the cart contains {string}")
    public void cartContains(String product) {
        assertEquals("Your Cart", ctx.cartPage().title());
        assertTrue(ctx.cartPage().hasItem(product), "Cart should contain " + product);
    }

    @When("the user proceeds to checkout")
    public void checkout() { ctx.cartPage().checkout(); }
}
