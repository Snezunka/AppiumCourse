import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class FirstTest {
    private AppiumDriver driver;

    private By wikipediaSearch = new By.ById("org.wikipedia:id/search_container");
    private By clearSearchButton = new By.ById("org.wikipedia:id/search_close_btn");
    private By languageIcon = new By.ById("org.wikipedia:id/search_lang_button");
    private By articleTitle = new By.ById("org.wikipedia:id/view_page_title_text");
    private By searchResults = new By.ById("org.wikipedia:id/page_list_item_title");
    private By searchInput = new By.ById("org.wikipedia:id/search_src_text");
    private By searchEmptyMessage = new By.ById("org.wikipedia:id/search_src_text");
    private By wikipediaSearchText = new By.ByXPath("//*[contains(@text, 'Поиск по Википедии')]");
    private By searchField = new By.ByXPath("//*[contains(@text, 'Поиск')]");
    private By searchResult = new By.ByXPath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[@text='Object-oriented programming language']");
    private By englishLanguageText = new By.ByXPath("//*[@resource-id='org.wikipedia:id/localized_language_name' and @text='English']");

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
        waitForElementIsPresentAndSendKeys(searchField, "Text 'Поиск' is not present","java", 5);
        changeLanguageOnEnglishIfNeeded();
        waitForElementIsPresent(searchResult,"'Object-oriented programming language' text is not present", 10);
    }

    @Test
    public void testCancelSearch() {
        waitForElementIsPresentAndClick(wikipediaSearch, "Wikipedia search is not present", 5);
        waitForElementIsPresentAndSendKeys(searchInput, "Search input is not present", "java", 10);
        checkThatArticlesPresentInSearchResult();
        waitForElementIsPresentAndClear(searchInput, "Search input is not present", 5);
        checkThatArticlesIsNotPresentInSearchResult();
        waitForElementIsPresentAndClick(clearSearchButton, "X button to cancel search is not present", 5);
        waitForElementIsNotPresent(clearSearchButton, "X button to cancel search is present", 5);
    }

    private void checkThatArticlesPresentInSearchResult() {
        await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> Assert.assertTrue(driver.findElements(searchResults).size() >= 1));
    }

    private void checkThatArticlesIsNotPresentInSearchResult() {
        WebDriverWait driverWaiter = new WebDriverWait(driver, 10);
        driverWaiter.withMessage("Search results are still on page").until(ExpectedConditions.visibilityOfElementLocated(searchEmptyMessage));
    }

    @Test
    public void testCompareArticleTitle() {
        waitForElementIsPresentAndClick(wikipediaSearch, "Wikipedia search is not present", 5);
        waitForElementIsPresentAndSendKeys(searchInput, "Search input is not present", "java", 10);
        changeLanguageOnEnglishIfNeeded();
        waitForElementIsPresent(searchResult,"'Object-oriented programming language' text is not present", 10);
        waitForElementIsPresentAndClick(searchResult, "'Object-oriented programming language' text is not present", 1);
        WebElement articleTitleElement = waitForElementIsPresent(articleTitle, "Article title is not present", 5);
        Assert.assertEquals("Actual value of article title is not correct", "Java (programming language)", articleTitleElement.getText());
    }

    @Test
    public void testSearchWordExistInEachResult() {
        String searchWord = "java";
        waitForElementIsPresentAndClick(wikipediaSearch, "Wikipedia search is not present", 5);
        waitForElementIsPresentAndSendKeys(searchInput, "Search input is not present", searchWord, 10);
        checkThatArticlesPresentInSearchResult();
        checkSearchWordInSearchResult(searchWord);
    }

    private void checkSearchWordInSearchResult(String expectedWord) {
          for (Object element: driver.findElements(searchResults)) {
              Assert.assertTrue(((WebElement) element).getText().toLowerCase().contains(expectedWord));
          }
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

    private void waitForElementIsPresentAndClear(By by, String errorMessage, int timeoutInSeconds) {
        WebElement element = waitForElementIsPresent(by, errorMessage, timeoutInSeconds);
        element.clear();
    }

    private void changeLanguageOnEnglishIfNeeded() {
        WebElement languageElement = driver.findElement(languageIcon);
        String languageText = languageElement.getText();
        if (!languageText.equals("EN")) {
            languageElement.click();
            driver.findElement(englishLanguageText).click();
        }
    }
    private boolean waitForElementIsNotPresent(By by, String errorMessage, int timeoutInSeconds) {
        WebDriverWait driverWaiter = new WebDriverWait(driver, timeoutInSeconds);
        return driverWaiter.withMessage(errorMessage).until(ExpectedConditions.invisibilityOfElementLocated(by));
    }
}
