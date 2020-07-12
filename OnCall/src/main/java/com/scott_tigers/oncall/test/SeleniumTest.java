package com.scott_tigers.oncall.test;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumTest {

    public static void main(String[] args) throws InterruptedException {
//	WebDriver driver;
//	private Map<String, Object> vars;
	JavascriptExecutor js;
	System.setProperty("webdriver.chrome.driver", "c:\\chromedriver.exe");

	ChromeOptions chromeProfile = new ChromeOptions();
//	chromeProfile.addArguments("chrome.switches", "--disable-extensions");
	chromeProfile.addArguments("user-data-dir=" + "C:\\Users\\bruscob\\AppData\\Local\\Google\\Chrome\\User Data");

	// Initialize browser
	WebDriver driver = new ChromeDriver(chromeProfile);

//	driver = new ChromeDriver();
	js = (JavascriptExecutor) driver;
//	vars = new HashMap<String, Object>();

	driver.get("https://tt.amazon.com/");
//	driver.manage().window().setSize(targetSize);
	driver.manage().window().setSize(new Dimension(1936, 974));
	driver.findElement(By.linkText("Settings")).click();
	driver.findElement(By.linkText("2020-06-01 Secondary Backlog")).click();
	TimeUnit.SECONDS.sleep(10);
	driver.findElement(By.linkText(" Export first 100 results")).click();
	TimeUnit.SECONDS.sleep(10);
	driver.quit();

    }

}
