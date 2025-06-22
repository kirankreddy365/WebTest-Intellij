package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LogEntry;
import base.TestBase;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.initializeReport();
        ExtentManager.addSystemInfo("Test Suite", context.getName());
        ExtentManager.addSystemInfo("Start Time", new java.util.Date().toString());
        ExtentManager.addSystemInfo("Docker Environment", "Selenium Grid with VNC enabled");
        ExtentManager.addSystemInfo("VNC Ports", "Chrome: 5900, Firefox: 5901, Edge: 5902");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.startTest(result.getName());
        ExtentManager.addTestInfo("Test Started: " + result.getName());
        ExtentManager.addTestInfo("Test Method: " + result.getMethod().getMethodName());
        ExtentManager.addTestInfo("Test Class: " + result.getTestClass().getName());
        
        // Log test parameters if any
        if (result.getParameters().length > 0) {
            ExtentManager.addTestInfo("Test Parameters: " + Arrays.toString(result.getParameters()));
        }
        
        // Log browser information
        logBrowserInfo(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.addTestPass("Test Passed: " + result.getName());
        ExtentManager.addTestInfo("Duration: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
        
        // Log any success messages
        if (result.getMethod().getDescription() != null) {
            ExtentManager.addTestInfo("Description: " + result.getMethod().getDescription());
        }
        
        // Capture final page state for successful tests
        capturePageState(result, "SUCCESS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Get the retry count from the result
        int retryCount = 0;
        if (result.getMethod().getRetryAnalyzer(result) != null) {
            RetryAnalyzer retryAnalyzer = (RetryAnalyzer) result.getMethod().getRetryAnalyzer(result);
            retryCount = retryAnalyzer.getRetryCount();
        }

        // Log failure details
        ExtentManager.addTestFail("Test Failed: " + result.getName());
        ExtentManager.addTestFail("Failure Reason: " + result.getThrowable().getMessage());
        
        // Log stack trace
        if (result.getThrowable().getStackTrace() != null) {
            ExtentManager.addTestFail("Stack Trace: " + Arrays.toString(result.getThrowable().getStackTrace()));
        }

        // Only take screenshot if all retries are exhausted
        if (retryCount >= MAX_RETRY_COUNT) {
            String screenshotPath = captureScreenshot(result);
            if (screenshotPath != null) {
                ExtentManager.getTest().addScreenCaptureFromPath(screenshotPath);
                ExtentManager.addTestInfo("Screenshot captured: " + screenshotPath);
            }
            
            // Capture detailed page state for failed tests
            capturePageState(result, "FAILURE");
        } else {
            ExtentManager.addTestInfo("Retrying test: " + result.getName() + 
                " (Attempt " + (retryCount + 1) + " of " + MAX_RETRY_COUNT + ")");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.addTestSkip("Test Skipped: " + result.getName());
        if (result.getThrowable() != null) {
            ExtentManager.addTestSkip("Skip Reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.addSystemInfo("End Time", new java.util.Date().toString());
        ExtentManager.addSystemInfo("Total Tests", String.valueOf(context.getAllTestMethods().length));
        ExtentManager.addSystemInfo("Passed Tests", String.valueOf(context.getPassedTests().size()));
        ExtentManager.addSystemInfo("Failed Tests", String.valueOf(context.getFailedTests().size()));
        ExtentManager.addSystemInfo("Skipped Tests", String.valueOf(context.getSkippedTests().size()));
        ExtentManager.endTest();
    }

    private String captureScreenshot(ITestResult result) {
        try {
            // Get the test class instance
            Object testClass = result.getInstance();
            if (testClass instanceof TestBase) {
                // Get the WebDriver instance from the test class
                WebDriver driver = ((TestBase) testClass).getDriver();
                if (driver != null) {
                    // Take screenshot
                    return ScreenshotUtil.captureScreenshot(driver, result.getName());
                }
            }
        } catch (Exception e) {
            ExtentManager.addTestWarning("Failed to capture screenshot: " + e.getMessage());
        }
        return null;
    }
    
    private void logBrowserInfo(ITestResult result) {
        try {
            Object testClass = result.getInstance();
            if (testClass instanceof TestBase) {
                WebDriver driver = ((TestBase) testClass).getDriver();
                if (driver != null) {
                    ExtentManager.addTestInfo("Browser: " + driver.getClass().getSimpleName());
                    ExtentManager.addTestInfo("Current URL: " + driver.getCurrentUrl());
                    ExtentManager.addTestInfo("Page Title: " + driver.getTitle());
                }
            }
        } catch (Exception e) {
            ExtentManager.addTestWarning("Failed to log browser info: " + e.getMessage());
        }
    }
    
    private void capturePageState(ITestResult result, String state) {
        try {
            Object testClass = result.getInstance();
            if (testClass instanceof TestBase) {
                WebDriver driver = ((TestBase) testClass).getDriver();
                if (driver != null) {
                    // Capture page source
                    String pageSource = driver.getPageSource();
                    String pageSourcePath = savePageSource(pageSource, result.getName(), state);
                    if (pageSourcePath != null) {
                        ExtentManager.addTestInfo("Page source saved: " + pageSourcePath);
                    }
                    
                    // Capture browser console logs
                    String consoleLogsPath = captureConsoleLogs(driver, result.getName(), state);
                    if (consoleLogsPath != null) {
                        ExtentManager.addTestInfo("Console logs saved: " + consoleLogsPath);
                    }
                }
            }
        } catch (Exception e) {
            ExtentManager.addTestWarning("Failed to capture page state: " + e.getMessage());
        }
    }
    
    private String savePageSource(String pageSource, String testName, String state) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + state + "_" + timestamp + ".html";
            String filePath = ConfigManager.getProperty("screenshot.path") + "pagesource/" + fileName;
            
            // Create directory if it doesn't exist
            File directory = new File(ConfigManager.getProperty("screenshot.path") + "pagesource/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            FileWriter writer = new FileWriter(filePath);
            writer.write(pageSource);
            writer.close();
            
            return filePath;
        } catch (IOException e) {
            ExtentManager.addTestWarning("Failed to save page source: " + e.getMessage());
            return null;
        }
    }
    
    private String captureConsoleLogs(WebDriver driver, String testName, String state) {
        try {
            List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll();
            if (!logs.isEmpty()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = testName + "_" + state + "_" + timestamp + "_console.log";
                String filePath = ConfigManager.getProperty("screenshot.path") + "logs/" + fileName;
                
                // Create directory if it doesn't exist
                File directory = new File(ConfigManager.getProperty("screenshot.path") + "logs/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                
                FileWriter writer = new FileWriter(filePath);
                for (LogEntry log : logs) {
                    writer.write(log.getTimestamp() + " " + log.getLevel() + " " + log.getMessage() + "\n");
                }
                writer.close();
                
                return filePath;
            }
        } catch (Exception e) {
            ExtentManager.addTestWarning("Failed to capture console logs: " + e.getMessage());
        }
        return null;
    }
} 