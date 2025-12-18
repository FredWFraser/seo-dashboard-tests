package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class DashboardPage {

    private WebDriver driver;

    // URL of your local Flask dashboard
    private final String url = "http://127.0.0.1:5000";

    // Locators
    private final By liveUrlRadio = By.cssSelector("input[name='source_type'][value='url']"); // radio button
    private final By urlInputField = By.id("url");            // URL input
    private final By analyzeButton = By.id("runChecksBtn");   // Analyze button
    private final By resultCards = By.cssSelector(".card");   // Result cards

    // Constructor
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    // Load dashboard
    public void open() {
        driver.get(url);
    }

    // Select live URL radio button
    public void selectLiveUrl() {
        WebElement radio = driver.findElement(liveUrlRadio);
        if (!radio.isSelected()) {
            radio.click();
        }
    }

    // Enter a URL
    public void enterUrl(String urlToAnalyze) {
        WebElement input = driver.findElement(urlInputField);
        input.clear();
        input.sendKeys(urlToAnalyze);
    }

    // Click Analyze button
    public void clickAnalyze() {
        driver.findElement(analyzeButton).click();
    }

    // Wait for results cards to appear
    public boolean waitForResultCards(int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(resultCards));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Optionally get all result cards
    public List<WebElement> getResultCards() {
        return driver.findElements(resultCards);
    }
    
    public void uploadHtmlFile(Path filePath) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait until the file input exists in the DOM
        WebElement fileInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("file"))
        );

        fileInput.sendKeys(filePath.toAbsolutePath().toString());

        driver.findElement(By.id("runChecksBtn")).click();
    }
    
    public void waitForDashboardToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(analyzeButton));
    }

    
}


