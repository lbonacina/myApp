package myApp.test.selenium;

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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.inject.Inject;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@RunWith(Arquillian.class)
public class CreateUserTest {
// ------------------------------ FIELDS ------------------------------

    @Inject Logger log;

    @ArquillianResource URL applicationPath;

    @Drone WebDriver browser;

// -------------------------- STATIC METHODS --------------------------

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        return WarBuilder.buildWar();
    }

// -------------------------- OTHER METHODS --------------------------

    public static final By NAV_MENU = By.id("menu_form:nav_menu");
    public static final By USERS_PAGE_BTN = By.id("menu_form:users_page_btn");
    public static final By ADD_USER_BTN = By.id("users_form:add_user_btn");

    public static final By PAGE_TITLE = By.className("page-title");

    public static final By FIRSTNAME = By.id("users_form:firstName");
    public static final By LASTNAME = By.id("users_form:lastName");
    public static final By EMAIL = By.id("users_form:email");
    public static final By PHONE = By.id("users_form:phone");
    public static final By USERNAME = By.id("users_form:username");
    public static final By SAVE_BTN = By.id("users_form:save_btn");


    @Test
    @InSequence(1)
    public void testCreateUser() throws Throwable {

        browser.manage().window().maximize();

        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.myApp", Locale.ENGLISH);

        browser.get(applicationPath + "login.jsf");
        CommonScenarions.insertLoginCredentials(browser, "admin", "admin");

        new WebDriverWait(browser,20).
                until(ExpectedConditions.presenceOfElementLocated(NAV_MENU)) ;

        // access user page
        browser.findElement(USERS_PAGE_BTN).click();

        new WebDriverWait(browser,20).
                until(ExpectedConditions.textToBePresentInElement(PAGE_TITLE,
                        resourceBundle.getString("page_title_users") + " - " +
                        resourceBundle.getString("user_pages_welcome_fieldset_header")));

        // click add user
        browser.findElement(ADD_USER_BTN).click();

        new WebDriverWait(browser,20).
                until(ExpectedConditions.textToBePresentInElement(PAGE_TITLE,
                        resourceBundle.getString("page_title_users") + " - " +
                        resourceBundle.getString("user_pages_new_fieldset_header")));

        fillUserForm("Mark","Smith","mark.smith@mail.com","1234567890","marks",new String[] {"User"});

        // save
        Thread.sleep(1000L);
        browser.findElement(SAVE_BTN).click();
        Thread.sleep(1000L);

        final By NEW_USER = By.xpath("//table[@class='ui-selectlistbox-list']//td[text()='Mark Smith']") ;

        new WebDriverWait(browser,20).
                until(ExpectedConditions.presenceOfElementLocated(NEW_USER));

        CommonScenarions.doLogout(browser);
    }

    @Test
    @InSequence(2)
    public void testNewUserFirstLogin() throws Throwable {

        browser.manage().window().maximize();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.myApp", Locale.ENGLISH);

        // test new user
        CommonScenarions.insertLoginCredentials(browser, "marks", "12345678");

        new WebDriverWait(browser,20).
                until(ExpectedConditions.textToBePresentInElement(By.xpath("//div[@id='users_form:account_panel_header']/span[@class='ui-panel-title']"),
                        resourceBundle.getString("page_title_account")));
    }


    private void fillUserForm(String firstName, String lastName, String email, String phone, String username, String[] roles)  throws Throwable {

        // input values
        WebElement firstNameInput = browser.findElement(FIRSTNAME);
        WebElement lastNameInput = browser.findElement(LASTNAME);
        WebElement emailInput = browser.findElement(EMAIL);
        WebElement phoneInput = browser.findElement(PHONE);
        WebElement usernameInput = browser.findElement(USERNAME);

        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
        emailInput.clear();
        emailInput.sendKeys(email);
        phoneInput.clear();
        phoneInput.sendKeys(phone);
        usernameInput.clear();
        usernameInput.sendKeys(username);

        for (String s : roles) {
            By ROLE = By.xpath("//li[text()='" + s + "']");
            WebElement roleElem = browser.findElement(ROLE);

            Actions builder = new Actions(browser);
            Actions hoverOverRegistrar = builder.doubleClick(roleElem);
            hoverOverRegistrar.perform();
            Thread.sleep(1000L);
        }
    }
}
