package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.shared.Lambda;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LTTRTicket {

    private static final String TICKET_ID_MARKER = "TICKETID";
    private static final String SEARCH_TEMPLATE = "https://tt.amazon.com/search?category=&assigned_group=&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date=&tags=&case_type=&building_id=&root_cause_details="
	    + TICKET_ID_MARKER
	    + "+&search=Search%21";

    private static final boolean DEBUG = false;

    private String area;
    private String assignee;
    private String count;
    private String description;
    private String email;
    private String notes;
    private String release;
    private String releaseDate;
    private String searchUrl;
    private String state;
    private String ticketId;
    private String tickets;
    private String ticketsPerWeek;
    private String ticketUrl;
    private String to;
    private String totalTickets;

    public LTTRTicket() {
    }

    public LTTRTicket(WebElement webRow) {
	List<WebElement> cells = webRow.findElements(By.tagName("td"));

	ticketId = getValue(cells, 0);
	description = getValue(cells, 1).split("\n")[0];
	tickets = getValue(cells, 2);
	ticketsPerWeek = getValue(cells, 3);
	ticketUrl = getTicketUrl(cells);
	searchUrl = SEARCH_TEMPLATE.replaceAll("(.*?)" + TICKET_ID_MARKER + "(.*)", "$1" + ticketId + "$2");

	if (DEBUG) {
	    List<WebElement> rows = webRow.findElements(By.tagName("td"));
	    IntStream.range(0, rows.size()).forEach(n -> {
		System.out.println(n + ". " + rows.get(n).getText());
	    });
	}
    }

    private String getTicketUrl(List<WebElement> cells) {
	String href = Optional
		.ofNullable(cells)
		.filter(Lambda.minSize(1))
		.map(Lambda.getElement(0))
		.map(c -> c.findElements(By.tagName("a")))
		.filter(Lambda.minSize(1))
		.map(Lambda.getElement(0))
		.filter(Objects::nonNull)
		.map(element -> element.getAttribute("href"))
		.orElse(null);

	return HREF.stream()
		.map(h -> h.getURL(href, ticketId))
		.filter(Objects::nonNull)
		.findFirst()
		.orElse("");
    }

    private static String getValue(List<WebElement> cells, int index) {

	return Optional
		.ofNullable(cells)
		.filter(td -> td.size() > index)
		.map(Lambda.getElement(index))
		.map(WebElement::getText)
		.orElse("");
    }

    public boolean validTicket() {
	return ticketId.length() > 0;
    }

    enum HREF {
	NONE {
	    @Override
	    String getURL(String href, String ticketId) {
		return href == null ? "" : null;
	    }
	},
	SIM {
	    @Override
	    String getURL(String href, String ticketId) {
		return href.contains("issues.amazon.com/issues/") ? "https://issues.amazon.com/issues/" + ticketId
			: null;
	    }
	},
	JIRA {
	    @Override
	    String getURL(String href, String ticketId) {
		return href.contains("rds-jira.amazon.com/browse/") ? href : null;
	    }
	};

	private static Stream<HREF> stream() {
	    return Stream.of(values());
	}

	abstract String getURL(String href, String ticketId);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((ticketId == null) ? 0 : ticketId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LTTRTicket other = (LTTRTicket) obj;
	if (ticketId == null) {
	    if (other.ticketId != null)
		return false;
	} else if (!ticketId.equals(other.ticketId))
	    return false;
	return true;
    }

    public String getArea() {
	return area;
    }

    public void setArea(String area) {
	this.area = area;
    }

    public String getAssignee() {
	return assignee;
    }

    public void setAssignee(String assignee) {
	this.assignee = assignee;
    }

    public String getCount() {
	return count;
    }

    public void setCount(String count) {
	this.count = count;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    public String getRelease() {
	return release;
    }

    public void setRelease(String release) {
	this.release = release;
    }

    public String getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
	this.releaseDate = releaseDate;
    }

    public String getSearchUrl() {
	return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
	this.searchUrl = searchUrl;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getTicketId() {
	return ticketId;
    }

    public void setTicketId(String ticketId) {
	this.ticketId = ticketId;
    }

    public String getTickets() {
	return tickets;
    }

    public void setTickets(String tickets) {
	this.tickets = tickets;
    }

    public String getTicketsPerWeek() {
	return ticketsPerWeek;
    }

    public void setTicketsPerWeek(String ticketsPerWeek) {
	this.ticketsPerWeek = ticketsPerWeek;
    }

    public String getTicketUrl() {
	return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
	this.ticketUrl = ticketUrl;
    }

    public String getTo() {
	return to;
    }

    public void setTo(String to) {
	this.to = to;
    }

    public String getTotalTickets() {
	return totalTickets;
    }

    public void setTotalTickets(String totalTickets) {
	this.totalTickets = totalTickets;

    }

}
