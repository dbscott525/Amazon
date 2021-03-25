package com.amazon.amsoperations.bean;

import java.beans.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Lambda;
import com.amazon.amsoperations.shared.Properties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private String currentTicketsPerWeek;
    private String date;
    private String description;
    private String email;
    private String notes;
    private String release;
    private String releaseDate;
    private String send;
    private String searchUrl;
    private String state;
    private String ticketId;
    private String tickets;
    private String ticketsPerWeek;
    private String ticket;
    private String to;
    private String totalTickets;
    private String type;
    private String delta;

    public LTTRTicket() {
    }

    public LTTRTicket(WebElement webRow) {
	List<WebElement> cells = webRow.findElements(By.tagName("td"));

	ticketId = getValue(cells, 0);
	description = getValue(cells, 1).split("\n")[0];
	tickets = getValue(cells, 2);
	ticketsPerWeek = getValue(cells, 3);
	ticket = getTicketUrl(cells);
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

    @JsonProperty(Properties.AREA)
    public String getArea() {
	return area;
    }

    public void setArea(String area) {
	this.area = area;
    }

    @JsonProperty(Properties.ASSIGNEE)
    public String getAssignee() {
	return assignee;
    }

    public void setAssignee(String assignee) {
	this.assignee = assignee;
    }

    @JsonProperty(Properties.COUNT)
    public String getCount() {
	return count;
    }

    public void setCount(String count) {
	this.count = count;
    }

    @JsonProperty(Properties.CURRENT_TICKETS_PER_WEEK)
    public String getCurrentTicketsPerWeek() {
	return currentTicketsPerWeek;
    }

    public void setCurrentTicketsPerWeek(String currentTicketsPerWeek) {
	this.currentTicketsPerWeek = currentTicketsPerWeek;
    }

    @JsonProperty(Properties.DATE)
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @JsonProperty(Properties.DESCRIPTION)
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @JsonProperty(Properties.EMAIL)
    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getNotes() {
	return notes;
    }

    @JsonProperty(Properties.NOTES)
    public void setNotes(String notes) {
	this.notes = notes;
    }

    @JsonProperty(Properties.RELEASE)
    public String getRelease() {
	return release;
    }

    public void setRelease(String release) {
	this.release = release;
    }

    @JsonProperty(Properties.RELEASE_DATE)
    public String getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
	this.releaseDate = releaseDate;
    }

    @JsonProperty(Properties.SEND)
    public String getSend() {
	return send;
    }

    public void setSend(String send) {
	this.send = send;
    }

    @JsonProperty(Properties.SEARCH_URL)
    public String getSearchUrl() {
	return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
	this.searchUrl = searchUrl;
    }

    @JsonProperty(Properties.STATE)
    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    @JsonProperty(Properties.TICKET_ID)
    public String getTicketId() {
	return ticketId;
    }

    public void setTicketId(String ticketId) {
	this.ticketId = ticketId;
    }

    @JsonProperty(Properties.TICKETS)
    public String getTickets() {
	return tickets;
    }

    public void setTickets(String tickets) {
	this.tickets = tickets;
    }

    @JsonProperty(Properties.TICKETS_PER_WEEK)
    public String getTicketsPerWeek() {
	return ticketsPerWeek;
    }

    public void setTicketsPerWeek(String ticketsPerWeek) {
	this.ticketsPerWeek = ticketsPerWeek;
    }

    @JsonProperty(Properties.TICKET)
    public String getTicket() {
	return ticket;
    }

    public void setTicket(String ticket) {
	this.ticket = ticket;
    }

    @JsonProperty(Properties.TO)
    public String getTo() {
	return to;
    }

    public void setTo(String to) {
	this.to = to;
    }

    @JsonProperty(Properties.TOTAL_TICKETS)
    public String getTotalTickets() {
	return totalTickets;
    }

    public void setTotalTickets(String totalTickets) {
	this.totalTickets = totalTickets;

    }

    @Transient
    public Double getDoubleTicketsPerWeek() {
	return ticketsPerWeek == null ? 0 : Double.valueOf(ticketsPerWeek);
    }

    @Transient
    public String getMonth() {
	return date == null ? null : Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.YEAR_MONTH);
    }

    @JsonProperty(Properties.TYPE)
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @JsonProperty(Properties.DELTA)
    public String getDelta() {
	return delta;
    }

    public void setDelta(String delta) {
	this.delta = delta;
    }

    public boolean isCandidate() {
	return "Candidate".equals(state);
    }

    public boolean isNotTotal() {
	return !"Total".equals(ticket);
    }

    public void update(Map<String, LTTRTicket> lttrMap) {
	if ("Availability".equals(area)) {
	    return;
	}
	LTTRTicket frequencyTicket = lttrMap.get(ticket);
	String tpw = frequencyTicket == null ? "0" : frequencyTicket.getTicketsPerWeek();
	if (date != null && Dates.ONLINE_SCHEDULE.getDateFromString(date).compareTo(new Date()) > 0) {
	    ticketsPerWeek = tpw;
	    currentTicketsPerWeek = "";
	} else {
	    currentTicketsPerWeek = tpw;
	}
    }

    @Transient
    public Integer getDeltaInteger() {
	return Integer.parseInt(delta);
    }

    @Transient
    public int getIntTickets() {
	return Integer.parseInt(tickets);
    }
}
