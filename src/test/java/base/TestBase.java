package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import utils.ConfigManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TestBase {
    protected WebDriver driver;
    protected static final String GRID_URL = ConfigManager.getProperty("grid.url");
    protected static final boolean USE_GRID = Boolean.parseBoolean(ConfigManager.getProperty("use.grid"));
    protected static final String browser = ConfigManager.getProperty("browser");
    protected static final boolean isHeadless = Boolean.parseBoolean(ConfigManager.getProperty("isHeadless"));
    protected static final String env = ConfigManager.getProperty("env");


    @BeforeSuite
    public void beforeSuite() {
        // Add any suite-level setup here
        System.out.println("Environment is - "+env);
        System.out.println("Browser is - "+browser);
    }

    @BeforeMethod
    public void setup() {
        initializeDriver();
        //configureDriver();
        driver.get(ConfigManager.getProperty("base.url"));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing driver: " + e.getMessage());
            } finally {
                driver = null;
            }
        }
    }

    @AfterSuite
    public void afterSuite() {
        // Add any suite-level cleanup here
    }

    private void initializeDriver() {
        if (USE_GRID) {
            initializeRemoteDriver(browser);
        } else {
            initializeLocalDriver(browser);
        }
    }

    private void initializeLocalDriver(String browser) {
        switch (browser) {
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
            Map<String, Object> capabilities = new HashMap<>();
            capabilities.put("browserName", browser);
//            capabilities.put("platform", ConfigManager.getProperty("platform"));
//            capabilities.put("version", ConfigManager.getProperty("browser.version"));
            
            driver = new RemoteWebDriver(new URL(GRID_URL), new ChromeOptions().addArguments("--remote-allow-origins=*"));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to initialize remote driver: " + e.getMessage());
        }
    }

//    private void configureDriver() {
//        driver.manage().window().maximize();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigManager.getImplicitWait()));
////        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getPageLoadTimeout()));
////        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ConfigManager.getScriptTimeout()));
//    }

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