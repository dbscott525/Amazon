package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.Lambda;
import com.scott_tigers.oncall.shared.WebElements;

public class PickNextLTTRTicket extends Utility {

    public static void main(String[] args) throws InterruptedException {

	new PickNextLTTRTicket().run();
    }

    private void run() throws InterruptedException {
	List<String> planTickets = EngineerFiles.LTTR_PLAN_TICKETS
		.readCSVToPojo(LTTRTicket.class)
		.stream()
		.map(LTTRTicket::getTicketUrl)
		.collect(Collectors.toList());

	BiConsumer<LTTRTicket, WebDriver> ticketProcessor = (ticket, driver) -> processTicket(driver, ticket);

	System.setProperty(WebElements.WEBDRIVER_CHROME_DRIVER_PROPERTY, Constants.CHROMEDRIVER_EXE_LOCATION);
	ChromeOptions chromeProfile = new ChromeOptions();
	chromeProfile
		.addArguments(WebElements.USER_DATA_DIR_PROPERTY + Constants.CHROME_USER_DATA_LOCATION);
	WebDriver driver = new ChromeDriver(chromeProfile);

	driver.get(LTTRPage.TOP.getUrl());
	Thread.sleep(2000);
	driver
		.findElement(By.id(WebElements.TBL_PRIORITIZATION_ID))
		.findElements(By.tagName(WebElements.TABLE_ROW_TAG))
		.stream()
		.map(LTTRTicket::new)
		.filter(LTTRTicket::validTicket)
		.filter(webTicket -> !planTickets.contains(webTicket.getTicketUrl()))
		.findFirst()
		.ifPresent(ticket -> {
//		    processTicket(driver, ticket);
		    ticketProcessor.accept(ticket, driver);

		    Json.print(ticket);

		    LTTRList.addTicket(ticket);

		    launchUrl(ticket.getTicketUrl());
		});

	driver.quit();
    }

    private void processTicket(WebDriver driver, LTTRTicket ticket) {
	ticket.setEmail(Constants.REPLACE_ME_EMAIL);
	ticket.setTo("SDM");
	ticket.setState("Candidate");

	driver.get(ticket.getSearchUrl());
	try {
	    TimeUnit.SECONDS.sleep(2);
	} catch (InterruptedException e) {
	}
	List<WebElement> o1 = driver.findElements(By.xpath("//*[contains(text(),'matches')]"));
	String totalTickets = Optional
		.ofNullable(o1)
		.filter(Lambda.minSize(1))
		.map(Lambda.getElement(0))
		.map(WebElement::getText)
		.map(x -> x.replaceAll("Displaying .*?of (\\d*) matches", "$1"))
		.orElse("unknown");
	ticket.setTotalTickets(totalTickets);
    }

    enum LTTRList {
	EMAIL(EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA, "ticketId", "email", "to", "tickets", "description", "ticketUrl",
		"searchUrl", "totalTickets"),
	PLAN(EngineerFiles.LTTR_PLAN_TICKETS, "ticketUrl", "ticketsPerWeek", "description", "area", "release",
		"releaseDate", "totalTickets", "assignee", "ticketId", "state", "notes");

	private EngineerFiles fileType;
	private String[] columnHeaders;

	LTTRList(EngineerFiles fileType, String... columnHeaders) {
	    this.fileType = fileType;
	    this.columnHeaders = columnHeaders;
	}

	private static void addTicket(LTTRTicket ticket) {
	    Stream.of(values()).forEach(foo -> {
		foo.addTicketToList(ticket);
	    });

	}

	void addTicketToList(LTTRTicket ticket) {
	    List<LTTRTicket> newList = Stream
		    .concat(
			    Stream.of(ticket),
			    fileType.readCSVToPojo(LTTRTicket.class).stream())
		    .collect(Collectors.toList());

	    fileType.write(w -> w.CSV(newList, columnHeaders).archive());
	}

    }

}
