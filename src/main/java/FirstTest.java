import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;

public class FirstTest {
    private AppiumDriver driver;

    private By wikipediaSearchText = new By.ByXPath("//*[contains(@text, 'Поиск по Википедии')]");
    private By searchField = new By.ByXPath("//*[contains(@text, 'Поиск')]");

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String userDir = System.getProperty("user.dir");

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "AndroidTestEmulator");
        capabilities.setCapability("platformVersion", "7.1");
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("appPackage", "org.wikipedia");
        capabilities.setCapability("appActivity", ".main.MainActivity");
        capabilities.setCapability("app", userDir + "/apks/org.wikipedia.apk");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @Test
    public void firstTest() {
        waitForElementIsPresentAndClick(wikipediaSearchText, "Text 'Поиск по Википедии' is not present", 5);
        waitForElementIsPresent(searchField, "Text 'Поиск' is not present");
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private WebElement waitForElementIsPresent(By by, String errorMessage, int timeoutInSeconds) {
        WebDriverWait driverWaiter = new WebDriverWait(driver, timeoutInSeconds);
        return driverWaiter.withMessage(errorMessage).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private WebElement waitForElementIsPresent(By by, String errorMessage) {
        return waitForElementIsPresent(by, errorMessage, 5);
    }

    private void waitForElementIsPresentAndClick(By by, String errorMessage, int timeoutInSecond) {
        WebElement element = waitForElementIsPresent(by, errorMessage, timeoutInSecond);
        element.click();
    }

    private void waitForElementIsPresentAndSendKeys(By by, String errorMessage, String searchText, int timeoutInSeconds) {
        WebElement element = waitForElementIsPresent(by, errorMessage, timeoutInSeconds);
        element.sendKeys(searchText);
    }
}
