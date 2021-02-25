package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.LargeVolumeTicketReader;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class CreateTicketFlowReport extends Utility implements Command {

    private static String URL_TEMPLATE = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=START%2CEND&modified_date=&tags=&case_type=&building_id=&search=Search%21";

    public static void main(String[] args) throws Exception {
	new CreateTicketFlowReport().run();
    }

    @Override
    public void run() throws Exception {

	System.out.println("Creating Ticket Flow Report");

	TicketFlowAggregator tfa = new TicketFlowAggregator();

	String startDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedDate(), -366);
	LargeVolumeTicketReader.getStream(r -> r
		.urlTemplate(URL_TEMPLATE)
		.startDate(startDate))
		.filter(this::validData)
		.filter(TT::include)
		.forEach(tfa::newTicket);

	List<TTCMetric> ttcMetrics = tfa.getTtcMetrics();

	List<TicketMetric> result = tfa.getMetrics();

	OpenTicketComputer openTicketComputer = new OpenTicketComputer(
		getTicketStreamFromUrl(URL.OPEN_CUSTOMER_ISSUE_TICKETS).filter(TT::include).count());

	result
		.stream()
		.sorted(Comparator.reverseOrder())
		.forEach(openTicketComputer::updateOpenTickets);

	EngineerFiles.TIME_TO_CLOSE.write(writer -> writer.CSV(ttcMetrics, Properties.DATE, Properties.DAYS));
	EngineerFiles.TIME_TO_CLOSE_GRAPH.launch();
	EngineerFiles.TICKET_FLOW_REPORT.write(writer -> writer
		.CSV(result,
			Properties.DATE,
			Properties.CREATED,
			Properties.RESOLVED,
			Properties.OPEN));
	EngineerFiles.TICKET_FLOW_GRAPH.launch();
	EngineerFiles.TICKET_FLOW_GRAPH_WITH_OPENED.launch();
    }

    private boolean validData(TT tt) {
	return tt.getCreateDate().compareTo(tt.getResolvedDate()) <= 0;
    }

    private class OpenTicketComputer {

	private int openTickets;

	public OpenTicketComputer(long openTickets) {
	    this.openTickets = (int) openTickets;
	}

	public void updateOpenTickets(TicketMetric ticketMetric) {
	    openTickets += (ticketMetric.getResolvedDateCount() - ticketMetric.getCreateDateCount());
	    ticketMetric.setOpen(openTickets);
	}
    }
}
