package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

import config.TestConfig;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
    	WebDriverManager.chromedriver().setup();
    	driver = new ChromeDriver();
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

