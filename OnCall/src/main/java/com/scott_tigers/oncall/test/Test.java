package com.scott_tigers.oncall.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.utility.LTTRPage;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	WebDriver driver = getWebDriver();
	driver.get(LTTRPage.GRAPH.getUrl());
	List<WebElement> tds = driver
		.findElement(By.xpath("//table/tbody/tr"))
		.findElements(By.tagName("td"));

	double average = IntStream
		.range(2, 8)
		.mapToObj(tds::get)
		.map(td -> td.getAttribute("innerHTML"))
		.map(Double::parseDouble)
		.mapToDouble(x -> x)
		.average()
		.orElse(Double.NaN);

	System.out.println("average=" + (average));

	driver.quit();
    }

    @SuppressWarnings("unused")
    private void run21() throws Exception {
	Calendar cld = Calendar.getInstance();
	cld.set(Calendar.YEAR, 2020);
	cld.set(Calendar.WEEK_OF_YEAR, 35);
	Date result = cld.getTime();
	System.out.println("result=" + (result));
    }

}