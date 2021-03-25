package com.amazon.amsoperations.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumTest {

    public static void main(String[] args) throws InterruptedException {
	// Optional. If not specified, WebDriver searches the PATH for chromedriver.
	System.setProperty("webdriver.chrome.driver", "P:\\bin\\chromedriver.exe");

//	WebDriver driver = new ChromeDriver();
	String url = "https://rds-portal.corp.amazon.com/lttr/reports/prioritization?week=2020-33&range=2020-39&team=Aurora+MySQL+-+Engine";
//	driver.get("http://www.google.com/");

	ChromeOptions chromeProfile = new ChromeOptions();
//	chromeProfile.addArguments("chrome.switches", "--disable-extensions");
	chromeProfile.addArguments("user-data-dir=" + "C:\\Users\\bruscob\\AppData\\Local\\Google\\Chrome\\User Data");

	// Initialize browser
	WebDriver driver = new ChromeDriver(chromeProfile);

	driver.get(url);
	Thread.sleep(5000); // Let the user actually see something!
	WebElement tableElement = driver.findElement(By.id("tblPrioritization"));
	List<WebElement> rowsList = tableElement.findElements(By.tagName("tr"));
	System.out.println("rowsList.size()=" + (rowsList.size()));
	rowsList.stream().limit(5).forEach(row -> {
	    List<WebElement> td = row.findElements(By.tagName("td"));
	    System.out.println("td.size()=" + (td.size()));
	    td.stream().forEach(cell -> {
		System.out.println("cell.getText()=" + (cell.getText()));
	    });
	});
//	WebElement searchBox = driver.findElement(By.name("q"));
//	searchBox.sendKeys("ChromeDriver");
//	searchBox.submit();
//	Thread.sleep(5000); // Let the user actually see something!
	driver.quit();
    }

}
