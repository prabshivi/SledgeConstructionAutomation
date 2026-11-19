package com.saucedemo.stepdefinitions;

import com.saucedemo.context.TestContext;
import io.cucumber.java.en.*;

public class LoginSteps {
    private final TestContext ctx;
    public LoginSteps(TestContext ctx) { this.ctx = ctx; }

    @Given("the user is on the SauceDemo login page")
    public void onLoginPage() { ctx.loginPage().open(); }

    @When("the user logs in with username {string} and password {string}")
    public void login(String user, String pass) { ctx.loginPage().login(user, pass); }
}
