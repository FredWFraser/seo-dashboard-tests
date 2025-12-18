package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.ResultsPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class DashboardSmokeTest extends BaseTest {

    @Test
    public void dashboardSmokeTest_debug() {
        DashboardPage dashboard = new DashboardPage(driver);

        // Open the dash board
        //dashboard.open();

        // Basic title check
        String title = driver.getTitle();
        System.out.println("TITLE = " + title);
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");

        // Select the live URL radio button
        dashboard.selectLiveUrl();

        // Enter URL (replace with your real URL if desired)
        String testUrl = "https://www.proximagranite.com/";
        dashboard.enterUrl(testUrl);
        dashboard.clickAnalyze();

        // Wait for result cards
        boolean resultsVisible = dashboard.waitForResultCards(5);
        Assert.assertTrue(resultsVisible, "Expected result cards to be visible");
        
        // Step 2: Verify at least one result card exists
        List<WebElement> rawCards = driver.findElements(By.className("card"));

        System.out.println("Number of result cards found: " + rawCards.size());

        Assert.assertFalse(rawCards.isEmpty(), "Expected at least one result card on the results page");
        
        // end step 2 code
        
        // Step 3: Print category titles for verification
        ResultsPage results = new ResultsPage(driver);
        List<String> categories = results.getCategoryTitles();

        System.out.println("Categories found on the results page:");
        for (String category : categories) {
            System.out.println(" - " + category);
        }
        
        // end step 3 code
        
        // Step 4: Verify each category has either issues or "No issues found"
        for (String category : categories) {
            List<String> issues = results.getIssuesForCategory(category);

            System.out.println("Verifying category: " + category + " -> " + issues);

            Assert.assertFalse(issues.isEmpty(),
                    "Category '" + category + "' should have at least one issue or 'No issues found'");
        }
        
        // end step 4 code
        
        // Behavioral validation: analysis output exists
        Assert.assertFalse(
                results.getIssuesForCategory("Image Dimensions").isEmpty(),
                "Image Dimensions category should return analysis output"
        );

        Assert.assertFalse(
                results.getIssuesForCategory("WebP Suggestions").isEmpty(),
                "WebP Suggestions category should return analysis output"
        );
        


        // Retrieve result cards
        List<WebElement> cards = dashboard.getResultCards();

        // ---- Debug: print the raw text of every card ----
        System.out.println("==== Result Cards ====");
        cards.forEach(card -> {
            System.out.println("CARD:");
            System.out.println("[" + card.getText() + "]");
            System.out.println("-----------------------");
        });
        System.out.println("=======================");

        // ---- Normalize card text and search for expected content ----

        // Example target text (lower case, whitespace-normalized)
        String expected = "needed to prevent cls and improve core web vitals.";

        boolean foundExpected = cards.stream().anyMatch(card -> {
            // Normalize whitespace and case
            String normalized = card.getText()
                    .replaceAll("\\s+", " ")   // collapse whitespace
                    .trim()
                    .toLowerCase();
            return normalized.contains(expected);
        });

        Assert.assertTrue(foundExpected,
                "Expected at least one card to contain text matching: " + expected);
    }
    
    @Test
    public void dashboardFileUploadSmokeTest() {
    	//driver.get("http://127.0.0.1:5000/");
    	System.out.println("TITLE AFTER NAV = " + driver.getTitle());
    	System.out.println("DEBUG URL before file upload = " + driver.getCurrentUrl());
        DashboardPage dashboard = new DashboardPage(driver);

        Path testFile = Paths.get(
            "src/test/resources/fixtures/simple-page.html"
        );

        dashboard.uploadHtmlFile(testFile);

        // Reuse existing verification logic
        List<WebElement> cards = dashboard.getResultCards();

        System.out.println("File upload result cards found: " + cards.size());
        Assert.assertFalse(cards.isEmpty(),
            "Expected result cards after file upload");

        // Optional: log categories for visibility
        for (WebElement card : cards) {
            System.out.println("CARD:\n[" + card.getText() + "]");
            System.out.println("-----------------------");
        }
    }
    
    @Test
    public void dashboardEmptyFileUploadShowsWarning() {
        DashboardPage dashboard = new DashboardPage(driver);
        
        dashboard.waitForDashboardToLoad();

        // Default state is file upload selected
        dashboard.clickAnalyze();

        ResultsPage results = new ResultsPage(driver);
        
        System.out.println("=== DEBUG: Page text dump ===");
        System.out.println(driver.findElement(By.tagName("body")).getText());
        System.out.println("=== END DEBUG ===");

        // Assert warning message is shown
        Assert.assertTrue(
            results.isWarningDisplayed(),
            "Expected warning message when no file is uploaded"
        );

        // Assert no result cards are shown
        Assert.assertTrue(
            results.getResultCards().isEmpty(),
            "Expected no SEO result cards when no file is uploaded"
        );

        // Assert back-to-dashboard link exists
        Assert.assertTrue(
            results.isBackLinkDisplayed(),
            "Expected back-to-dashboard link on empty upload result"
        );
    }
    
    @Test
    public void dashboardEmptyLiveUrlShowsWarning() {
        DashboardPage dashboard = new DashboardPage(driver);

        // Make sure the dash board is ready
        dashboard.waitForDashboardToLoad();

        // Step 1: select live URL radio button
        dashboard.selectLiveUrl();

        // Step 2: leave URL input empty and click Analyze
        dashboard.clickAnalyze();

        // Step 3: results page validation
        ResultsPage results = new ResultsPage(driver);

        // Warning message must be displayed
        Assert.assertTrue(
            results.isWarningDisplayed(),
            "Expected warning when live URL is empty"
        );

        // There should be no SEO result cards
        Assert.assertTrue(
            results.getResultCards().isEmpty(),
            "Expected no SEO results when live URL is empty"
        );
    }
    
    @Test
    public void dashboardBadLiveUrlShowsWarning() {
        DashboardPage dashboard = new DashboardPage(driver);

        // Ensure dash board is ready
        dashboard.waitForDashboardToLoad();

        // Step 1: select live URL radio button
        dashboard.selectLiveUrl();

        // Step 2: enter a bad URL
        dashboard.enterUrl("http://nonexistent.localhost/");

        // Step 3: click Analyze
        dashboard.clickAnalyze();

        // Step 4: results page validation
        ResultsPage results = new ResultsPage(driver);

        // Warning message must be displayed
        Assert.assertTrue(
            results.isWarningDisplayed(),
            "Expected warning when a bad URL is entered"
        );

        // There should be no SEO result cards
        Assert.assertTrue(
            results.getResultCards().isEmpty(),
            "Expected no SEO results for a bad URL"
        );
    }




}

