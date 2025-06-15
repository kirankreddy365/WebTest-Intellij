package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties;
    private static final String CONFIG_FILE = "src/test/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        return properties.getProperty(key);
    }

//    public static String getBaseUrl() {
//        return getProperty("base.url");
//    }
//
//    public static String getBrowser() {
//        return getProperty("browser");
//    }
//
//    public static boolean isHeadless() {
//        return Boolean.parseBoolean(getProperty("isHeadless"));
//    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait"));
    }

    public static String getEnvironment() {
        return getProperty("env");
    }

    public static String getTestDataPath() {
        return getProperty("test.data.path");
    }

    public static String getScreenshotPath() {
        return getProperty("screenshot.path");
    }

    public static String getReportPath() {
        return getProperty("report.path");
    }
} 