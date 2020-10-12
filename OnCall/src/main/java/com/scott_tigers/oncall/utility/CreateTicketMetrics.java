package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateTicketMetrics extends Utility {

    public static void main(String[] args) {
	new CreateTicketMetrics().run();
    }

    private String lastDate;
    private String startDate;

    private void run() {

	TicketSummaryMetrics ticketSummaryMetrics = new TicketSummaryMetrics();

	startDate = getCompletedShifts()
		.map(Shift::getDate)
		.sorted()
		.findFirst()
		.orElse("error - should never happen");

	lastDate = Dates.SORTABLE.getLastMondayFormattedDate();

	long ticketsClosed = getTicketsClosed();

	int engineerWeeks = getCompletedShifts()
		.map(Shift::getUids)
		.map(List<String>::size)
		.mapToInt(Integer::intValue)
		.sum();

	ticketSummaryMetrics.setTicketsClosedPerEngineerPerWeek((double) ticketsClosed / engineerWeeks);
	ticketSummaryMetrics.setNewTicketsPerWeek(
		(double) getSumValue(TicketMetric::getCreateDateCount) / getCompletedShifts().count());

	List<TicketSummaryRow> metricList = Stream
		.of(TicketSummary.values())
		.map(ticketSummary -> ticketSummary.getRow(ticketSummaryMetrics))
		.collect(Collectors.toList());

	EngineerFiles.TICKET_SUMMARY.write(w -> w.CSV(metricList, TicketSummaryRow.class));
    }

    private long getTicketsClosed() {

	// In the history the was anomalous week where a large number of tickets were
	// closed
	// through a Customer Support clean up of tickets where many tickets were set to
	// 7 Day Auto Resolve

	int maxTickets = getResolvedStream()
		.summaryStatistics()
		.getMax();

	int average = (int) getResolvedStream()
		.filter(x -> x != maxTickets)
		.summaryStatistics()
		.getAverage();

	long ticketsClosed = getResolvedStream()
		.map(count -> count == maxTickets ? average : count)
		.summaryStatistics()
		.getSum();

	return ticketsClosed;
    }

    private IntStream getResolvedStream() {
	return getTicketMetricStream().map(x -> x.getResolvedDateCount())
		.mapToInt(x -> x);
    }

    private int getSumValue(Function<TicketMetric, Integer> valueGetter) {
	return getTicketMetricStream()
		.map(valueGetter)
		.mapToInt(Integer::intValue)
		.sum();
    }

    private Stream<TicketMetric> getTicketMetricStream() {
	return EngineerFiles.TICKET_FLOW_REPORT
		.readCSVToPojo(TicketMetric.class)
		.stream()
		.filter(m -> m.getDate().compareTo(startDate) >= 0)
		.filter(m -> m.getDate().compareTo(lastDate) <= 0);
    }

    private Stream<Shift> getCompletedShifts() {
	return getShiftStream().filter(s -> s.isBefore(lastDate));
    }

    private static class TicketSummaryRow {
	private String label;
	private Double value;

	public TicketSummaryRow(TicketSummary ticketSummary, double value) {
	    this.value = value;
	    label = ticketSummary.getLabel();
	}

	public String getLabel() {
	    return label;
	}

	public void setLabel(String label) {
	    this.label = label;
	}

	public Double getValue() {
	    return value;
	}

	public void setValue(Double value) {
	    this.value = value;
	}
    }

    enum TicketSummary {
	Tickets_Closed_Per_Engineer_Per_Week {

	    @Override
	    protected TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics) {
		return getRow(ticketSummaryMetrics.getTicketsClosedPerEngineerPerWeek());
	    }

	},
	CIT_Size {
	    @Override
	    protected TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics) {
		return getRow(CIT_SIZE);
	    }
	},
	Tickets_Closed_By_CIT_Per_Week {
	    @Override
	    protected TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics) {
		return getRow(getTicketsClosedPerWeek(ticketSummaryMetrics));
	    }

	},
	New_Tickets_Per_Week {
	    @Override
	    protected TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics) {
		return getRow(ticketSummaryMetrics.getNewTicketsPerWeek());
	    }
	},
	Ticket_Delta_Per_Week {
	    @Override
	    protected TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics) {
		return getRow(
			ticketSummaryMetrics.getNewTicketsPerWeek() - getTicketsClosedPerWeek(ticketSummaryMetrics));
	    }
	};

	private static final int CIT_SIZE = 7;

	protected abstract TicketSummaryRow getRow(TicketSummaryMetrics ticketSummaryMetrics);

	String getLabel() {
	    return toString().replaceAll("_", " ");
	}

	TicketSummaryRow getRow(double value) {
	    return new TicketSummaryRow(this, value);
	}

	double getTicketsClosedPerWeek(TicketSummaryMetrics ticketSummaryMetrics) {
	    return CIT_SIZE * ticketSummaryMetrics.getTicketsClosedPerEngineerPerWeek();
	}
    }

    private class TicketSummaryMetrics {
	private double ticketsClosedPerEngineerPerWeek;
	private double newTicketsPerWeek;

	public double getTicketsClosedPerEngineerPerWeek() {
	    return ticketsClosedPerEngineerPerWeek;
	}

	public void setTicketsClosedPerEngineerPerWeek(double ticketsClosedPerEngineerPerWeek) {
	    this.ticketsClosedPerEngineerPerWeek = ticketsClosedPerEngineerPerWeek;
	}

	public double getNewTicketsPerWeek() {
	    return newTicketsPerWeek;
	}

	public void setNewTicketsPerWeek(double newTicketsPerWeek) {
	    this.newTicketsPerWeek = newTicketsPerWeek;
	}
    }

}
