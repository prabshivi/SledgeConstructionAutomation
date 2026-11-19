package com.saucedemo.stepdefinitions;

import com.saucedemo.context.TestContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutSteps {
    private final TestContext ctx;
    public CheckoutSteps(TestContext ctx) { this.ctx = ctx; }

    @When("the user enters checkout information {string}, {string} and {string}")
    public void enterInfo(String first, String last, String zip) {
        assertEquals("Checkout: Your Information", ctx.stepOnePage().title());
        ctx.stepOnePage().fillInformation(first, last, zip);
    }

    @When("the user continues to the overview")
    public void continueToOverview() { ctx.stepOnePage().continueToOverview(); }

    @Then("the overview lists {string}")
    public void overviewLists(String product) {
        assertEquals("Checkout: Overview", ctx.stepTwoPage().title());
        assertTrue(ctx.stepTwoPage().hasItem(product));
    }

    @When("the user finishes the order")
    public void finish() { ctx.stepTwoPage().finish(); }

    @Then("the order confirmation {string} is displayed")
    public void confirmation(String message) {
        assertEquals("Checkout: Complete!", ctx.completePage().title());
        assertEquals(message, ctx.completePage().header());
    }
}
