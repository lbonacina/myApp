package myApp.test.selenium;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@RunWith(Arquillian.class)
public class LoginAndLogoutTest {
// ------------------------------ FIELDS ------------------------------

    @Inject Logger log;

    @ArquillianResource URL applicationPath;

    @Drone WebDriver browser;

// -------------------------- STATIC METHODS --------------------------

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        // TODO : port tests on IE ?
        //File file = new File("C:\\Users\\eluibon\\Documents\\Workspace\\SeleniumDrivers\\IEDriverServer.exe");
        //System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
        return WarBuilder.buildWar();
    }

// -------------------------- OTHER METHODS --------------------------

    public static final By NAV_MENU = By.id("menu_form:nav_menu");
    public static final By LOGGED_USER_FULL_NAME = By.xpath("//span[@id='logged_user_full_name']");

    @Test
    @InSequence(1)
    public void testLoginAndLogout() {

        browser.get(applicationPath + "login.jsf");
        CommonScenarions.insertLoginCredentials(browser, "admin", "admin");

        WebElement navMenu = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(NAV_MENU);
                    }
                });

        WebElement welcome = browser.findElement(LOGGED_USER_FULL_NAME);
        Assert.assertEquals("User: Paul Smith", welcome.getText());

        CommonScenarions.doLogout(browser);
    }

    /* test the menu visibility for admin and user */
    /* TODO : can we create a simple method to check the composition of menu against the different roles ? */
    @Test
    @InSequence(2)
    public void testPermissions() {

        final By PERSONAL_PAGE_BTN = By.id("menu_form:personal_page_btn");
        final By USERS_PAGE_BTN = By.id("menu_form:users_page_btn");
        final By LOGS_PAGE_BTN = By.id("menu_form:logs_page_btn");
        List<WebElement> usersPageBtn;
        List<WebElement> personalPageBtn;
        List<WebElement> logsPageBtn;

        browser.get(applicationPath + "login.jsf");

        CommonScenarions.insertLoginCredentials(browser, "admin", "admin");

        WebElement navMenu = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(NAV_MENU);
                    }
                });

        usersPageBtn = browser.findElements(USERS_PAGE_BTN);
        personalPageBtn = browser.findElements(PERSONAL_PAGE_BTN);
        logsPageBtn = browser.findElements(LOGS_PAGE_BTN);

        Assert.assertEquals(1, usersPageBtn.size());
        Assert.assertEquals(1, personalPageBtn.size());
        Assert.assertEquals(0, logsPageBtn.size());

        CommonScenarions.doLogout(browser);

        CommonScenarions.insertLoginCredentials(browser, "wakka", "wakka");

        navMenu = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(NAV_MENU);
                    }
                });

        usersPageBtn = browser.findElements(USERS_PAGE_BTN);
        personalPageBtn = browser.findElements(PERSONAL_PAGE_BTN);
        logsPageBtn = browser.findElements(LOGS_PAGE_BTN);

        Assert.assertEquals(1, usersPageBtn.size());
        Assert.assertEquals(1, personalPageBtn.size());
        Assert.assertEquals(1, logsPageBtn.size());

        CommonScenarions.doLogout(browser);
    }


    /**
     *
     */
    @Test
    @InSequence(3)
    public void testLockingUser() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.myApp", Locale.ENGLISH);

        final By LOGIN_ERROR_MSG = By.xpath("//div[@id='login_form:loginError_msg']//span[@class='ui-messages-error-summary']");

        browser.get(applicationPath + "login.jsf");

        // login, three attempts remaining

        CommonScenarions.insertLoginCredentials(browser, "admin", "a");

        WebElement errorMsg = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_ERROR_MSG);
                    }
                });

        Assert.assertEquals(resourceBundle.getString("genericLoginError"), errorMsg.getText());

        // login, two attempts remaining

        CommonScenarions.insertLoginCredentials(browser, "admin", "ad");

        errorMsg = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_ERROR_MSG);
                    }
                });

        Assert.assertEquals(resourceBundle.getString("genericLoginError"), errorMsg.getText());

        // login, one attempt remaining

        CommonScenarions.insertLoginCredentials(browser, "admin", "adm");

        errorMsg = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_ERROR_MSG);
                    }
                });

        Assert.assertEquals(resourceBundle.getString("genericLoginError"), errorMsg.getText());

        // account is locked, no attempt remaining

        CommonScenarions.insertLoginCredentials(browser, "admin", "adm");

        errorMsg = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_ERROR_MSG);
                    }
                });

        Assert.assertEquals(resourceBundle.getString("genericLoginError"), errorMsg.getText());

        // account is locked, no attempt remaining

        CommonScenarions.insertLoginCredentials(browser, "admin", "admin");

        errorMsg = (new WebDriverWait(browser, 10))
                .until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(LOGIN_ERROR_MSG);
                    }
                });

        Assert.assertEquals(resourceBundle.getString("genericLoginError"), errorMsg.getText());
    }

}
