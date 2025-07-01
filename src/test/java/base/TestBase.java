package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utils.ConfigManager;
import utils.ExtentManager;

import java.net.URL;

public class TestBase {
    protected WebDriver driver;
    protected WebDriverActions actions;
    protected static final String GRID_URL = ConfigManager.getProperty("grid.url");
    protected static final boolean USE_GRID = Boolean.parseBoolean(ConfigManager.getProperty("use.grid"));
    protected static final boolean isHeadless = Boolean.parseBoolean(ConfigManager.getProperty("isHeadless"));
    protected static final String env = ConfigManager.getProperty("env");

    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        try {
            initializeDriver(browser);
            actions = new WebDriverActions(driver);
            ExtentManager.addSystemInfo("Browser", browser);
            ExtentManager.addSystemInfo("Environment", env);
            driver.get(ConfigManager.getProperty("base.url"));
        } catch (Exception e) {
            ExtentManager.addTestFail("Failed to initialize driver: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                ExtentManager.addTestWarning("Error closing driver: " + e.getMessage());
            } finally {
                driver = null;
            }
        }
    }

    private void initializeDriver(String browser) {
        if (USE_GRID) {
            initializeRemoteDriver(browser);
        } else {
            initializeLocalDriver(browser);
        }
    }

    private void initializeLocalDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver(getChromeOptions());
                break;
            case "firefox":
                driver = new FirefoxDriver(getFirefoxOptions());
                break;
            case "edge":
                driver = new EdgeDriver(getEdgeOptions());
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private void initializeRemoteDriver(String browser) {
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    driver = new RemoteWebDriver(new URL(GRID_URL), getChromeOptions());
                    System.out.println("Chrome browser is lunched.");
                    break;
                case "firefox":
                    driver = new RemoteWebDriver(new URL(GRID_URL), getFirefoxOptions());
                    System.out.println("Firefox browser is lunched.");
                    break;
                case "edge":
                    driver = new RemoteWebDriver(new URL(GRID_URL), getEdgeOptions());
                    System.out.println("Edge browser is lunched.");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize remote driver: " + e.getMessage());
        }
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    private EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    public WebDriver getDriver() {
        return driver;
    }
} 