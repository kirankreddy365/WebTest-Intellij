package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.WebDriver;
import base.TestBase;

public class TestListener implements ITestListener {
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void onTestFailure(ITestResult result) {
        // Get the retry count from the result
        int retryCount = 0;
        if (result.getMethod().getRetryAnalyzer(result) != null) {
            RetryAnalyzer retryAnalyzer = (RetryAnalyzer) result.getMethod().getRetryAnalyzer(result);
            retryCount = retryAnalyzer.getRetryCount();
        }

        // Only take screenshot if all retries are exhausted
        if (retryCount >= MAX_RETRY_COUNT) {
            captureScreenshot(result);
        }
    }

    private void captureScreenshot(ITestResult result) {
        try {
            // Get the test class instance
            Object testClass = result.getInstance();
            if (testClass instanceof TestBase) {
                // Get the WebDriver instance from the test class
                WebDriver driver = ((TestBase) testClass).getDriver();
                if (driver != null) {
                    // Take screenshot
                    String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
                    System.out.println("Screenshot captured: " + screenshotPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Starting test suite: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Finished test suite: " + context.getName());
    }
} 