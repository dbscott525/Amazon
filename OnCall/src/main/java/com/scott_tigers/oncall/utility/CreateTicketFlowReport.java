package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class CreateTicketFlowReport extends Utility implements Command {

    private static final int ITERATIONS = 3;
    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_PER_ITERATION = DAYS_IN_YEAR / ITERATIONS + ITERATIONS;
    private static String URL_TEMPLATE = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=START%2CEND&modified_date=&tags=&case_type=&building_id=&search=Search%21";
    private static Dates datetype = Dates.TT_SEARCH;

    public static void main(String[] args) throws Exception {
	new CreateTicketFlowReport().run();
    }

    @Override
    public void run() throws Exception {
	TicketFlowAggregator tfa = new TicketFlowAggregator();

	getDateStream()
		.flatMap(this::getDateRangeTicketStream)
		.forEach(tfa::newTicket);

	List<TicketMetric> result = tfa.getMetrics();
	result.remove(result.size() - 1);

	OpenTicketComputer openTicketComputer = new OpenTicketComputer(
		getTicketStreamFromUrl(URL.OPEN_CUSTOMER_ISSUE_TICKETS).count());

	result
		.stream()
		.sorted(Comparator.reverseOrder())
		.forEach(openTicketComputer::updateOpenTickets);

	EngineerFiles.TICKET_FLOW_REPORT.write(writer -> writer
		.CSV(result,
			Properties.DATE,
			Properties.CREATED,
			Properties.RESOLVED,
			Properties.OPEN));

	waitForDataFileLaunch();
	EngineerFiles.TICKET_FLOW_GRAPH.launch();
	waitForDataFileLaunch();
	EngineerFiles.TICKET_FLOW_GRAPH_WITH_OPENED.launch();
    }

    private Stream<? extends TT> getDateRangeTicketStream(DateRange dateRange) {
	String url = URL_TEMPLATE.replaceAll("(.*?)START(.*?)END(.*)",
		"$1" + dateRange.getStartDate() + "$2" + dateRange.getEndDate() + "$3");
	try {
	    return getTicketStreamFromUrl(url);
	} catch (Exception e) {
	    Stream<TT> stream = Stream.empty();
	    return stream;
	}
    }

    private class OpenTicketComputer {

	private int openTickets;

	public OpenTicketComputer(long openTickets) {
	    this.openTickets = (int) openTickets;
	}

	public void updateOpenTickets(TicketMetric ticketMetric) {
	    ticketMetric.setOpen(openTickets);
	    openTickets += (ticketMetric.getResolvedDateCount() - ticketMetric.getCreateDateCount());
//	    openTickets += (ticketMetric.getCreateDateCount() - ticketMetric.getResolvedDateCount());
	}
    }

    private class DateRange {
	private String startDate;
	private String endDate;

	public DateRange(DateRange dateRange) {
	    startDate = dateRange.startDate;
	}

	public DateRange() {
	    startDate = datetype.getFormattedDelta(datetype.getFormattedString(), -DAYS_IN_YEAR);
	}

	public String getStartDate() {
	    return startDate;
	}

	public String getEndDate() {
	    return datetype.getFormattedDelta(startDate, DAYS_PER_ITERATION);
	}

	public void next() {
	    startDate = getEndDate();
	}

	@Override
	public String toString() {
	    return "DateRange [startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	public boolean hasNext() {
	    return datetype.getDateFromString(getStartDate()).compareTo(new Date()) <= 0;
	}

    }

    private Stream<DateRange> getDateStream() {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(

			new Iterator<DateRange>() {
			    private DateRange dateRange = new DateRange();

			    @Override
			    public boolean hasNext() {
				return dateRange.hasNext();
			    }

			    @Override
			    public DateRange next() {
				DateRange current = new DateRange(dateRange);
				dateRange.next();
				return current;
			    }
			},
			Spliterator.ORDERED),
		false);
    }
}
