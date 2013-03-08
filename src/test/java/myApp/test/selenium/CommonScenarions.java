package myApp.test.selenium;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * User: eluibon
 * Date: 14/12/12
 * Time: 14.40
 */
public class CommonScenarions {
// ------------------------------ FIELDS ------------------------------

    public static final By USERNAME = By.id("login_form:username_i");
    public static final By PASSWORD = By.id("login_form:password_i");
    public static final By SUBMIT_BTN = By.id("login_form:submit_btn");
    public static final By LOGOUT_BTN = By.id("menu_form:logout_btn");
    public static final By LOGIN_LABEL = By.id("login_form:form_lbl");

// -------------------------- STATIC METHODS --------------------------



    public static void insertLoginCredentials(WebDriver browser, String username, String password) {

        WebElement usernameInput = browser.findElement(USERNAME);
        WebElement passwordInput = browser.findElement(PASSWORD);
        WebElement submitButton = browser.findElement(SUBMIT_BTN);
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        submitButton.click();
    }

    public static void doLogout(WebDriver browser) {

        WebElement logoutButton = browser.findElement(LOGOUT_BTN);
        logoutButton.click();
        WebElement welcome = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_LABEL);
                    }
                });

    }
}
