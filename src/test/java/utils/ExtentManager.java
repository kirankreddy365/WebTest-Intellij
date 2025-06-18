package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ExtentManager {
    private static final ExtentReports extentReports = new ExtentReports();
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static boolean isInitialized = false;
    private static final Set<String> addedSystemInfo = new HashSet<>();

    public static void initializeReport() {
        if (!isInitialized) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = "test-output/ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("Vaneck Automation Report");
            sparkReporter.config().setReportName("Vaneck Test Execution Report");
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            
            extentReports.attachReporter(sparkReporter);
            
            // Add system information only once
            addSystemInfo("OS", System.getProperty("os.name"));
            addSystemInfo("Java Version", System.getProperty("java.version"));
            addSystemInfo("Browser", System.getProperty("browser", "chrome"));
            addSystemInfo("Environment", System.getProperty("env", "staging"));
            
            isInitialized = true;
        }
    }

    public static void startTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        extentTest.set(test);
    }

    public static void endTest() {
        if (extentTest.get() != null) {
            extentReports.flush();
        }
    }

    public static void addTestPass(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.PASS, message);
        }
    }

    public static void addTestFail(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.FAIL, message);
        }
    }

    public static void addTestSkip(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.SKIP, message);
        }
    }

    public static void addTestInfo(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.INFO, message);
        }
    }

    public static void addTestWarning(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.WARNING, message);
        }
    }

    public static void addSystemInfo(String key, String value) {
        // Only add system info if it hasn't been added before
        if (!addedSystemInfo.contains(key)) {
            extentReports.setSystemInfo(key, value);
            addedSystemInfo.add(key);
        }
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }
} 