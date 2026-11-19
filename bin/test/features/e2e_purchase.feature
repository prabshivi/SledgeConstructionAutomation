@e2e @smoke
Feature: SauceDemo purchase flow

  Scenario: Standard user completes an end-to-end purchase
    Given the user is on the SauceDemo login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page is displayed
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge shows 1 item
    When the user opens the cart
    Then the cart contains "Sauce Labs Backpack"
    When the user proceeds to checkout
    And the user enters checkout information "Shivi", "Prabhakar" and "E3R4G2"
    And the user continues to the overview
    Then the overview lists "Sauce Labs Backpack"
    When the user finishes the order
    Then the order confirmation "Thank you for your order!" is displayed
