package com.scott_tigers.oncall.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium {

    public static void main(String[] args) {
	// declaration and instantiation of objects/variables
//	System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
//	WebDriver driver = new FirefoxDriver();

	System.setProperty("webdriver.chrome.driver", "c:\\chromedriver.exe");

	ChromeOptions chromeProfile = new ChromeOptions();
//	chromeProfile.addArguments("chrome.switches", "--disable-extensions");
	chromeProfile.addArguments("user-data-dir=" + "C:\\Users\\bruscob\\AppData\\Local\\Google\\Chrome\\User Data");

	// Initialize browser
	WebDriver driver = new ChromeDriver(chromeProfile);

//	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//	String chromeProfile = "C:\\Users\\Tiuz\\AppData\\Local\\Google\\Chrome\\User Data\\Default";
//	String chromeProfile = "C:\\Users\\bruscob\\AppData\\Local\\Google\\Chrome\\User Data";
//	ArrayList<String> switches = new ArrayList<String>();
//	switches.add("--user-data-dir=" + chromeProfile);
//	capabilities.setCapability("chrome.switches", switches);
//	WebDriver driver = new ChromeDriver(capabilities);

//	ChromeOptions options = new ChromeOptions();
//	options.setCapability("--user-data-dir=" + chromeProfile);
//	driver = new ChromeDriver(options);
//	driver.get("http://www.google.com");

	// comment the above 2 lines and uncomment below 2 lines to use Chrome
	// System.setProperty("webdriver.chrome.driver","G:\\chromedriver.exe");
	// WebDriver driver = new ChromeDriver();

//	String baseUrl = "http://demo.guru99.com/test/newtours/";
//	String baseUrl = "https://rds-portal.corp.amazon.com/lttr/reports/weekly?week=2020-22&range=2020-28&team=Aurora+MySQL+-+Engine&graph=Line";
//	String baseUrl = "https://tt.amazon.com/";
//	String baseUrl = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=06%2F24%2F2020&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21#";
	String baseUrl = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=06%2F01%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21#";
	String expectedTitle = "Welcome: Mercury Tours";
	String actualTitle = "";

	// launch Fire fox and direct it to the Base URL
	driver.get(baseUrl);

	// get the actual value of the title
	actualTitle = driver.getTitle();

	List<WebElement> elements = driver.findElements(By.xpath("//a[@href]"));
	elements.stream().filter(we -> we.getText().contains("Export"))

		.forEach(we -> {
		    System.out.println("we.getTagName()=" + (we.getTagName()));
		    System.out.println("we.getClass()=" + (we.getClass()));
		    System.out.println("we.getText()=" + (we.getText()));
		});

	try {
//	    TimeUnit.SECONDS.sleep(10);
	    WebElement exportLink = driver.findElement(By.linkText("Export max results to Excel (may timeout)"));
	    System.out.println(" exportLink.isEnabled()=" + (exportLink.isEnabled()));
	    WebDriverWait wait = new WebDriverWait(driver, 10);
	    wait.until(
		    ExpectedConditions.elementToBeClickable(By.linkText("Export max results to Excel (may timeout)")));
//	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//xpath_to_element")));
//	    System.out.println("exportLink.getTagName()=" + (exportLink.getTagName()));
//	    System.out.println("exportLink.getText()=" + (exportLink.getText()));
	    exportLink.click();
	    TimeUnit.SECONDS.sleep(10);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	/*
	 * compare the actual title of the page with the expected one and print the
	 * result as "Passed" or "Failed"
	 */
	if (actualTitle.contentEquals(expectedTitle)) {
	    System.out.println("Test Passed!");
	} else {
	    System.out.println("Test Failed");
	}

	// close Fire fox
	driver.close();
    }

}
