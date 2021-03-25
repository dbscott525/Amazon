package com.amazon.amsoperations.shared;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.amazon.amsoperations.bean.Engineer;

public class Util {

    public static String getDateIncrementString(Date date, int dayIncrement, String dateFormat) {
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, dayIncrement);
	String dateString = sdf.format(c.getTime());
	return dateString;
    }

    public static String getEngineerEmails(List<Engineer> engineers) {
	return engineers
		.stream()
		.map(Engineer::getEmail)
		.collect(Collectors.joining(";"));
    }

    public static String getEngineerToList(List<Engineer> engineers) {
	return engineers
		.stream()
		.map(Engineer::getFirstName)
		.collect(Collectors.joining(", "))
		.replaceAll("(.+,)(.+)", "$1 and$2");
    }

    public static boolean foundIn(String target, String searchString) {
	return target
		.toLowerCase()
		.contains(searchString
			.toLowerCase()
			.trim());
    }

    public static Function<String, String> toAmazonEmail() {
	return uid -> uid + "@amazon.com";
    }

    public static Integer getCaseId(String ticketUrl) {
	return Optional
		.ofNullable(ticketUrl)
		.map(url -> url.replaceAll(".*?(\\d).*?", "$1"))
		.filter(Util::isDigits)
		.map(Integer::parseInt)
		.orElse(0);
    }

    private static boolean isDigits(String line) {
	return line.matches("[0-9]+");
    }

    public static void waitForDataFileLaunch() {
	try {
	    TimeUnit.SECONDS.sleep(3);
	} catch (InterruptedException e) {
	}
    }

    public static void sleep(int seconds) {
	try {
	    TimeUnit.SECONDS.sleep(seconds);
	} catch (InterruptedException e) {
	}
    }

    public static WebDriver getWebDriver() {
	waitForUser("Close all Chrome browsers and hit Enter in the Console to continue");

	System.setProperty(WebElements.WEBDRIVER_CHROME_DRIVER_PROPERTY, Constants.CHROMEDRIVER_EXE_LOCATION);
	ChromeOptions chromeProfile = new ChromeOptions();
	chromeProfile
		.addArguments(WebElements.USER_DATA_DIR_PROPERTY + Constants.CHROME_USER_DATA_LOCATION);
	WebDriver driver = new ChromeDriver(chromeProfile);
	return driver;
    }

    public static void waitForUser(String message) {
	@SuppressWarnings("resource")
	Scanner scanner = new Scanner(System.in);
	System.out.println(message);
	scanner.nextLine();
	System.out.println("Continuing");
    }

    public static void copyToClipboard(String text) {
	Toolkit
		.getDefaultToolkit()
		.getSystemClipboard()
		.setContents(new StringSelection(text), null);
    }
}
