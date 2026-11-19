package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {
    public static final String URL = com.saucedemo.config.Config.baseUrl();

    public LoginPage(Page page) { super(page); }

    public LoginPage open() {
        page.navigate(URL);
        return this;
    }

    public void login(String user, String password) {
        fill("username", user);
        fill("password", password);
        click("login-button");
    }
}
