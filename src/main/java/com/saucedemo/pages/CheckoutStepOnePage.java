package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class CheckoutStepOnePage extends BasePage {
    public CheckoutStepOnePage(Page page) { super(page); }

    public String title() { return text("title"); }

    public void fillInformation(String first, String last, String zip) {
        fill("firstName", first);
        fill("lastName", last);
        fill("postalCode", zip);
    }

    public void continueToOverview() { click("continue"); waitForTitle("Checkout: Overview"); }
}
