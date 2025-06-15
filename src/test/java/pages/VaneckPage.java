package pages;

import base.WebDriverActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class VaneckPage {
    private final WebDriver driver;
    private final WebDriverActions actions;

    // Locators
    private static final By INVESTMENTS_MENU = By.xpath("//a[contains(text(), 'Investments')]");
    private static final By INVESTMENT_TYPE_DROPDOWN = By.xpath("//button[@id='investment-type']");
    private static final By DROPDOWN_OPTIONS = By.xpath("//div[@id='intestmentType']/div/button");
    private static final By DROPDOWN_SELECTED_OPTION = By.xpath("//button[@id='investment-type']/span");

    public VaneckPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new WebDriverActions(driver);
    }

    public void navigateToFundExplorer() {
        try {
            actions.waitForElementClickable(INVESTMENTS_MENU);
            actions.click(INVESTMENTS_MENU);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to Fund Explorer: " + e.getMessage());
        }
    }

    public void selectInvestmentType(String investmentType) {
        if (investmentType == null || investmentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Investment type cannot be null or empty");
        }

        try {
            actions.waitForElementClickable(INVESTMENT_TYPE_DROPDOWN);
            actions.click(INVESTMENT_TYPE_DROPDOWN);
            actions.selectDropdownValue(DROPDOWN_OPTIONS, investmentType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to select investment type '" + investmentType + "': " + e.getMessage());
        }
    }

    public boolean isMutualFundsSelected(String investmentType) {
        try {
            actions.waitForElementClickable(DROPDOWN_SELECTED_OPTION);
            String selectedText = actions.getText(DROPDOWN_SELECTED_OPTION).trim();
            return selectedText.equalsIgnoreCase(investmentType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify selected investment type: " + e.getMessage());
        }
    }
} 