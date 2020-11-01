package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.Lambda;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.Util;

public abstract class PickNextLttrSim extends Utility {

    protected void run() throws InterruptedException {
	List<String> planTickets = getTickertPlanFile()
		.readCSVToPojo(LTTRTicket.class)
		.stream()
		.map(LTTRTicket::getTicket)
		.collect(Collectors.toList());

	WebDriver driver = getWebDriver();

	getLttrTicketStream(driver)
		.filter(webTicket -> !planTickets.contains(webTicket.getTicket()))
		.findFirst()
		.ifPresent(ticket -> {
		    System.out.println("found ticket");
		    driver.get(ticket.getSearchUrl());
		    Util.sleep(2);
		    List<WebElement> o1 = driver.findElements(By.xpath("//*[contains(text(),'matches')]"));
		    String totalTickets = Optional
			    .ofNullable(o1)
			    .filter(Lambda.minSize(1))
			    .map(Lambda.getElement(0))
			    .map(WebElement::getText)
			    .map(x -> x.replaceAll("Displaying .*?of (\\d*) matches", "$1"))
			    .orElse("unknown");
		    ticket.setTotalTickets(totalTickets);
		    processTicket(driver, ticket);

		    Json.print(ticket);

		    LTTRList.addTicket(getTicketType(), ticket);

		    launchUrl(ticket.getTicket());
		});

	driver.quit();
    }

    protected abstract TicketType getTicketType();

    protected abstract EngineerFiles getTickertPlanFile();

    protected abstract void processTicket(WebDriver driver, LTTRTicket ticket);

    enum TicketType {
	HIGH_FREQUENCY, AUTOMATION
    }

    enum LTTRList {
	EMAIL(TicketType.HIGH_FREQUENCY, EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA, Properties.TICKET_ID,
		Properties.EMAIL, Properties.TO, Properties.TICKETS,
		Properties.DESCRIPTION, Properties.TICKET, Properties.SEARCH_URL, Properties.TOTAL_TICKETS),
	PLAN(TicketType.HIGH_FREQUENCY, EngineerFiles.LTTR_PLAN_TICKETS, Properties.TICKET,
		Properties.TICKETS_PER_WEEK, Properties.DESCRIPTION,
		Properties.AREA, Properties.RELEASE, Properties.RELEASE_DATE, Properties.TOTAL_TICKETS,
		Properties.ASSIGNEE, Properties.TICKET_ID, Properties.STATE, Properties.NOTES),
	AUTOMATION(TicketType.AUTOMATION, EngineerFiles.SIM_AUTOMATION_PLAN, Properties.TICKET, Properties.DESCRIPTION,
		Properties.TICKETS_PER_WEEK,
		Properties.TOTAL_TICKETS, Properties.NOTES, Properties.STATE);

	private EngineerFiles fileType;
	private String[] columnHeaders;
	private TicketType ticketType;

	LTTRList(TicketType ticketType, EngineerFiles fileType, String... columnHeaders) {
	    this.ticketType = ticketType;
	    this.fileType = fileType;
	    this.columnHeaders = columnHeaders;
	}

	private static void addTicket(TicketType ticketType, LTTRTicket ticket) {
	    Stream.of(values())
		    .filter(listType -> listType.ticketType == ticketType)
		    .forEach(listType -> listType.addTicketToList(ticket));
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
