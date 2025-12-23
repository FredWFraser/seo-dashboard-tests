package tests;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

import config.TestConfig;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        //driver = new ChromeDriver();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);

        // ---- Add this to skip ngrok warning ----
        ChromeDriver chromeDriver = (ChromeDriver) driver;

        // Enable Network domain first
        chromeDriver.executeCdpCommand("Network.enable", new HashMap<>());

        // Set the header to skip ngrok browser warning
        Map<String, Object> headers = new HashMap<>();
        headers.put("ngrok-skip-browser-warning", "true");
        chromeDriver.executeCdpCommand("Network.setExtraHTTPHeaders", Map.of("headers", headers));
        // ---- END ngrok warning bypass ----

        //driver.get("http://127.0.0.1:5000/");
        driver.get(TestConfig.getBaseUrl());
        driver.manage().window().maximize();
    }


    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

