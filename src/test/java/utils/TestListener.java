package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.WebDriver;
import base.TestBase;
import java.util.Arrays;

public class TestListener implements ITestListener {
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.initializeReport();
        ExtentManager.addSystemInfo("Test Suite", context.getName());
        ExtentManager.addSystemInfo("Start Time", new java.util.Date().toString());
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
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.addTestPass("Test Passed: " + result.getName());
        ExtentManager.addTestInfo("Duration: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
        
        // Log any success messages
        if (result.getMethod().getDescription() != null) {
            ExtentManager.addTestInfo("Description: " + result.getMethod().getDescription());
        }
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
} 