package tests;

import base.TestBase;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.VaneckPage;
import utils.ConfigManager;
import utils.RetryAnalyzer;

public class VaneckTest extends TestBase {
    private VaneckPage vaneckPage;

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testVaneckWebsite() {
        vaneckPage = new VaneckPage(driver);
        vaneckPage.navigateToFundExplorer();
        vaneckPage.selectInvestmentType("Mutual Funds");
        Assert.assertTrue(vaneckPage.isMutualFundsSelected("Mutual Funds"), "Mutual Funds option is not selected");
        System.out.println("Test passed in environment: "+ ConfigManager.getProperty("env"));
    }
} 