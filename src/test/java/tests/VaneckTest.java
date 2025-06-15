package tests;

import base.TestBase;
import org.testng.annotations.Test;
import pages.VaneckPage;
import utils.RetryAnalyzer;

public class VaneckTest extends TestBase {
    private VaneckPage vaneckPage;

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testVaneckWebsite() {
        vaneckPage = new VaneckPage(driver);
        vaneckPage.navigateToFundExplorer();
        vaneckPage.selectInvestmentType("Mutual Funds");
        vaneckPage.isMutualFundsSelected("Mutual Funds");
    }
} 