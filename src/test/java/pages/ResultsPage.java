package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ResultsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitUntilLoaded();
    }

    // Wait until the results page title is visible
    private void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
    }

    // Page title
    public String getPageTitle() {
        return driver.findElement(By.tagName("h1")).getText();
    }

    // Summary paragraph inside .insights
    public String getSummaryText() {
        List<WebElement> summaries = driver.findElements(By.cssSelector(".insights > p"));
        return summaries.isEmpty() ? "" : summaries.get(0).getText();
    }

    // AI Action Steps (if present)
    public List<String> getActionItems() {
        return driver.findElements(By.cssSelector(".insights ol li"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    // AI offline warning (optional)
    public boolean isAiOfflineWarningVisible() {
        return !driver.findElements(By.xpath("//*[contains(text(),'AI Insights are currently offline')]")).isEmpty();
    }

    // All results cards
    public List<WebElement> getResultCards() {
        return driver.findElements(By.cssSelector(".card"));
    }

    // Get category names (e.g., "SEO Basics", "Accessibility")
    public List<String> getCategoryTitles() {
        return driver.findElements(By.cssSelector(".card h3"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    // Get issues for a specific category
    public List<String> getIssuesForCategory(String categoryName) {
        // Find the card with this category
        WebElement card = driver.findElements(By.cssSelector(".card"))
                .stream()
                .filter(c -> c.findElement(By.tagName("h3")).getText().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElse(null);

        if (card == null) return List.of();

        List<WebElement> issues = card.findElements(By.cssSelector("ul li"));
        if (!issues.isEmpty()) {
            return issues.stream().map(WebElement::getText).collect(Collectors.toList());
        }

        // If no issues, check for the "No issues" message
        List<WebElement> noIssues = card.findElements(By.cssSelector(".no-issues"));
        if (!noIssues.isEmpty()) {
            return List.of("No issues found");
        }

        return List.of();  // fallback
    }
    
    public boolean categoryContainsText(String categoryName, String expectedText) {
        List<String> issues = getIssuesForCategory(categoryName);

        return issues.stream()
                .anyMatch(issue ->
                        issue.toLowerCase().contains(expectedText.toLowerCase())
                );
    }
    

    // Navigate back to dashboard
    public void clickBackToDashboard() {
        driver.findElement(By.cssSelector(".back-link a")).click();
    }
    
    public boolean isWarningDisplayed() {
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        return bodyText.contains("could not be analyzed")      // empty file / empty URL
            || bodyText.contains("couldn't be retrieved")      // bad URL
            || bodyText.contains("unable to load the url");     // extra safeguard
    }

    public boolean isBackLinkDisplayed() {
        return !driver.findElements(By.linkText("← Back to Dashboard")).isEmpty();
    }
}

