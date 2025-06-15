package base;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WebDriverActions {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    private final Actions actions;

    public WebDriverActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    // Generic Wait Method
    public <T> T waitFor(Function<WebDriver, T> condition) {
        return wait.until(condition);
    }

    // Element Location Methods
    public WebElement findElement(By locator) {
        return waitFor(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> findElements(By locator) {
        return waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // Wait Methods
    public void waitForElementVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForElementClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForElementToDisappear(By locator) {
        waitFor(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // Click Methods
    public void click(By locator) {
        waitForElementClickable(locator);
        findElement(locator).click();
    }

    public void jsClick(By locator) {
        WebElement element = findElement(locator);
        js.executeScript("arguments[0].click();", element);
    }

    public void actionsClick(By locator) {
        WebElement element = findElement(locator);
        actions.click(element).perform();
    }

    // Input Methods
    public void sendKeys(By locator, String text) {
        waitForElementVisible(locator);
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void sendKeysWithActions(By locator, String text) {
        WebElement element = findElement(locator);
        actions.sendKeys(element, text).perform();
    }

    // Text Methods
    public String getText(By locator) {
        waitForElementVisible(locator);
        return findElement(locator).getText();
    }

    public String getAttribute(By locator, String attribute) {
        return findElement(locator).getAttribute(attribute);
    }

    // Select and dropdown Methods
    public void selectByVisibleText(By locator, String text) {
        WebElement element = findElement(locator);
        new Select(element).selectByVisibleText(text);
    }

    public void selectByValue(By locator, String value) {
        WebElement element = findElement(locator);
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        WebElement element = findElement(locator);
        new Select(element).selectByIndex(index);
    }

    public void selectDropdownValue(By locator, String text) {
        waitForElementVisible(locator);
        List<WebElement> elements = findElements(locator);
        elements.stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Option not found: " + text))
                .click();
    }

    // Mouse Actions
    public void hoverOver(By locator) {
        WebElement element = findElement(locator);
        actions.moveToElement(element).perform();
    }

    public void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = findElement(sourceLocator);
        WebElement target = findElement(targetLocator);
        actions.dragAndDrop(source, target).perform();
    }

    // JavaScript Methods
    public void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})");
    }

    public void scrollToTop() {
        js.executeScript("window.scrollTo({top: 0, behavior: 'smooth'})");
    }

    // Frame Methods
    public void switchToFrame(By locator) {
        WebElement frame = findElement(locator);
        driver.switchTo().frame(frame);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // Alert Methods
    public void acceptAlert() {
        waitFor(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    public void dismissAlert() {
        waitFor(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    public String getAlertText() {
        waitFor(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }

    // Window Methods
    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    // Navigation Methods
    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    public void navigateForward() {
        driver.navigate().forward();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    // Element State Methods
    public boolean isElementSelected(By locator) {
        waitForElementVisible(locator);
        return findElement(locator).isSelected();
    }

    public boolean isElementEnabled(By locator) {
        return findElement(locator).isEnabled();
    }

    public boolean isElementDisplayed(By locator) {
        try {
            return findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
} 