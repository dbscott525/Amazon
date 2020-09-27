package com.scott_tigers.oncall.utility;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.bean.LTTRPlan;
import com.scott_tigers.oncall.bean.TicketCounts;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class CreateLTTRProjections extends Utility {

    private static final int PROJECTION_MONTHS = 12;
    private static final int NUMBER_OF_PROJECTION_MONTHS = 12;
    private static final int TRAILING_WEEKS = 4;

    public static void main(String[] args) throws Exception {
	new CreateLTTRProjections().run();
    }

    private String currentMonth;

    private void run() throws Exception {
	List<TicketCounts> ticketCounts = EngineerFiles.ENGINE_TICKET_COUNTS
		.readCSVToPojo(TicketCounts.class);

	double fourWeekTrailingAverage = ticketCounts
		.subList(ticketCounts.size() - TRAILING_WEEKS, ticketCounts.size())
		.stream()
		.mapToInt(TicketCounts::getTickets)
		.average()
		.orElse(0.0);

	currentMonth = Dates.YEAR_MONTH.getFormattedString();

	ProjectionAccumulator projectionAccumulator = new ProjectionAccumulator(fourWeekTrailingAverage);

	List<TicketReduction> reductionList = readFromUrl(URL.LTTR_PLAN, LTTRPlan.class)
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
	EngineerFiles.TICKET_REDUCTION_PROJECTION.write(w -> w.CSV(reductionList, TicketReduction.class));
    }

    private boolean onOrAfterCurrentMonth(TicketReduction ticketReduction) {
	return ticketReduction.getMonth().compareTo(currentMonth) >= 0;
    }

    private class LttrProjection {

	private LttrType lttrType;
	private LTTRPlan lttrPlan;

	public LttrProjection(LTTRPlan lttrPlan) {
	    this.lttrPlan = lttrPlan;
	    lttrType = Arrays
		    .asList(LttrType.values())
		    .stream()
		    .filter(type -> type.is(lttrPlan))
		    .findFirst()
		    .orElse(LttrType.DEFAULT);
	}

	public Stream<TicketReduction> getTicketReductions() {
	    return lttrType.getTicketReductions(lttrPlan);
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
	    boolean is(LTTRPlan lttrPlan) {
		return lttrPlan.getRelease().toLowerCase().contains("amvu");
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRPlan lttrPlan) {
		return getOneTimeStream(lttrPlan, .77);
	    }

	},
	SERVERLESS {
	    @Override
	    boolean is(LTTRPlan lttrPlan) {
		return lttrPlan.getArea().toLowerCase().matches(".*serverless|cp.*");
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRPlan lttrPlan) {
		return getOneTimeStream(lttrPlan, 1);
	    }
	},
	DEFAULT {
	    @Override
	    boolean is(LTTRPlan lttrPlan) {
		return true;
	    }

	    @Override
	    protected Stream<TicketReduction> getTicketReductions(LTTRPlan lttrPlan) {
		return getTicketAdoptionStream(lttrPlan);
	    }

	};

	abstract boolean is(LTTRPlan lttrPlan);

	protected abstract Stream<TicketReduction> getTicketReductions(LTTRPlan lttrPlan);

	private static Stream<TicketReduction> getOneTimeStream(LTTRPlan lttrPlan, double percentage) {
	    return Stream.of(new TicketReduction(lttrPlan.getMonth(), lttrPlan.getDoubleTicketsPerWeek() * percentage));
	}
    }

    private static class TicketReduction implements Comparable<TicketReduction> {
	private transient double tickets;
	private String month;
	private String estimatedTicketReduction;
	private String estimatedTicketsPerweek;

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

	@JsonProperty("Month")
	public String getMonth() {
	    return month;
	}

	public void setEstimatedTicketReduction(String estimatedTicketReduction) {
	    this.estimatedTicketReduction = estimatedTicketReduction;
	}

	public void setEstimatedTicketsPerweek(String estimatedTicketsPerweek) {
	    this.estimatedTicketsPerweek = estimatedTicketsPerweek;
	}

	@SuppressWarnings("unused")
	public String getEstimatedTicketReduction() {
	    return estimatedTicketReduction;
	}

	@SuppressWarnings("unused")
	public String getEstimatedTicketsPerweek() {
	    return estimatedTicketsPerweek;
	}

	@Override
	public int compareTo(TicketReduction o) {
	    return month.compareTo(o.month);
	}

    }

    private static Stream<TicketReduction> getTicketAdoptionStream(LTTRPlan lttrPlan) {

	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<TicketReduction>() {

			    Date currentDate = Dates.ONLINE_SCHEDULE.getDateFromString(lttrPlan.getDate());
			    double tickets = lttrPlan.getDoubleTicketsPerWeek();
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
