package com.scott_tigers.oncall.utility;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.Util;

public class CreateLTTRProjections extends Utility {

    private static final int PROJECTION_MONTHS = 12;
    private static final int NUMBER_OF_PROJECTION_MONTHS = 12;

    public static void main(String[] args) throws Exception {
	new CreateLTTRProjections().run();
    }

    private String currentMonth;

    private void run() throws Exception {

	new UpdateLttrPlanFrequencies().run();

	WebDriver driver = Util.getWebDriver();
	String graphUrl = LTTRPage.GRAPH.getUrl();
	driver.get(graphUrl);
	Double fourWeekTrailingAverage = Optional
		.ofNullable(driver)
		.map(d -> d.findElement(By.xpath("//table/tbody/tr/td[2]")))
		.map(e -> e.getAttribute("innerHTML"))
		.map(Double::parseDouble)
		.orElse(0.0);

	System.out.println("fourWeekTrailingAverage=" + (fourWeekTrailingAverage));

	driver.quit();

	currentMonth = Dates.YEAR_MONTH.getFormattedString();

	ProjectionAccumulator projectionAccumulator = new ProjectionAccumulator(fourWeekTrailingAverage);

	List<TicketReduction> reductionList = getLttrQuipPlan()
		.map(LttrProjection::new)
		.flatMap(LttrProjection::getTicketReductions)
		.collect(Collectors.groupingBy(TicketReduction::getMonth,
			Collectors.summingDouble(TicketReduction::getTickets)))
		.entrySet()
		.stream()
		.map(TicketReduction::new)
		.filter(this::onOrAfterCurrentMonth)
		.sorted()
		.limit(NUMBER_OF_PROJECTION_MONTHS)
		.peek(projectionAccumulator::accumulate)
		.collect(Collectors.toList());
	EngineerFiles.TICKET_REDUCTION_PROJECTION
		.write(w -> w.CSV(reductionList, Properties.MONTH, Properties.TICKETS_PER_MONTH));
	EngineerFiles.TICKET_REDUCTION_PROJECTION_GRAPH.launch();
    }

    private boolean onOrAfterCurrentMonth(TicketReduction ticketReduction) {
	return ticketReduction.getMonth().compareTo(currentMonth) >= 0;
    }

    private class LttrProjection {

	private LttrType lttrType;
	private LTTRTicket lttrTicet;

	public LttrProjection(LTTRTicket lttrTicet) {
	    this.lttrTicet = lttrTicet;
	    lttrType = Arrays
		    .asList(LttrType.values())
		    .stream()
		    .filter(type -> type.is(lttrTicet))
		    .findFirst()
		    .orElse(LttrType.DEFAULT);
	}

	public Stream<TicketReduction> getTicketReductions() {
	    return lttrType.getTicketReductions(lttrTicet);
	}

    }

    private class ProjectionAccumulator {

	private double ticketsPerWeek;
	private double accumulatedTicketReduction = 0;

	public ProjectionAccumulator(double ticketsPerWeek) {
	    this.ticketsPerWeek = ticketsPerWeek;
	}

	public void accumulate(TicketReduction ticketReduction) {
	    accumulatedTicketReduction += ticketReduction.getTickets();
	    ticketsPerWeek -= ticketReduction.getTickets();
	    ticketReduction.setEstimatedTicketReduction(format(accumulatedTicketReduction));
	    ticketReduction.setEstimatedTicketsPerweek(format(ticketsPerWeek));
	}

	private String format(double tickets) {
	    return new DecimalFormat("0.00").format(tickets);
	}

    }

    enum LttrType {
	AMVU {
	    @Override
	    boolean is(LTTRTicket lttrTicet) {
		return lttrTicet.getRelease().toLowerCase().contains("amvu");
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRTicket lttrTicet) {
		return getOneTimeStream(lttrTicet, .77);
	    }

	},
	SERVERLESS {
	    @Override
	    boolean is(LTTRTicket lttrTicet) {
		return lttrTicet.getArea().toLowerCase().matches(".*serverless|cp.*");
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRTicket lttrTicet) {
		return getOneTimeStream(lttrTicet, 1);
	    }
	},
	DEFAULT {
	    @Override
	    boolean is(LTTRTicket lttrTicet) {
		return true;
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRTicket lttrTicet) {
		return getTicketAdoptionStream(lttrTicet);
	    }

	};

	abstract boolean is(LTTRTicket lttrTicet);

	protected abstract Stream<TicketReduction> getTicketReductions(LTTRTicket lttrTicet);

	private static Stream<TicketReduction> getOneTimeStream(LTTRTicket lttrTicet, double percentage) {
	    return Stream
		    .of(new TicketReduction(lttrTicet.getMonth(), lttrTicet.getDoubleTicketsPerWeek() * percentage));
	}
    }

    private static class TicketReduction implements Comparable<TicketReduction> {
	private transient double tickets;
	private String month;
	private String estimatedTicketReduction;
	private String estimatedTicketsPerMonth;

	public TicketReduction(String month, double tickets) {
	    this.month = month;
	    this.tickets = tickets;
	}

	public TicketReduction(Entry<String, Double> entry) {
	    month = entry.getKey();
	    tickets = entry.getValue();
	}

	@JsonIgnore
	public double getTickets() {
	    return tickets;
	}

	@JsonProperty(Properties.MONTH)
	public String getMonth() {
	    return month;
	}

	public void setEstimatedTicketReduction(String estimatedTicketReduction) {
	    this.estimatedTicketReduction = estimatedTicketReduction;
	}

	public void setEstimatedTicketsPerweek(String estimatedTicketsPerweek) {
	    this.estimatedTicketsPerMonth = estimatedTicketsPerweek;
	}

	@SuppressWarnings("unused")
	public String getEstimatedTicketReduction() {
	    return estimatedTicketReduction;
	}

	@JsonProperty(Properties.TICKETS_PER_MONTH)
	public String getEstimatedTicketsPerMonth() {
	    return estimatedTicketsPerMonth;
	}

	@Override
	public int compareTo(TicketReduction o) {
	    return month.compareTo(o.month);
	}

    }

    private static Stream<TicketReduction> getTicketAdoptionStream(LTTRTicket lttrTicet) {

	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<TicketReduction>() {

			    Date currentDate = Dates.ONLINE_SCHEDULE.getDateFromString(lttrTicet.getDate());
			    double tickets = lttrTicet.getDoubleTicketsPerWeek();
			    int iteration = 0;

			    @Override
			    public boolean hasNext() {
				return iteration < PROJECTION_MONTHS;
			    }

			    @Override
			    public TicketReduction next() {
				String nextDate = Dates.YEAR_MONTH.getFormattedString(currentDate);
				currentDate = Dates.addMonth(currentDate);
				iteration++;
				return new TicketReduction(nextDate, tickets / PROJECTION_MONTHS);
			    }
			},
			Spliterator.ORDERED),
		false);
    }
}
