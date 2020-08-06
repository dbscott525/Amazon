package com.scott_tigers.oncall.utility;

import java.util.List;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateTicketFlowReport extends Utility {

    private static String URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=01%2F01%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21";

    public static void main(String[] args) throws Exception {
	new CreateTicketFlowReport().run();
    }

    private void run() throws Exception {

	TicketFlowAggregator tfa = new TicketFlowAggregator();

	getTicketStreamFromUrl(URL).forEach(tfa::newTicket);
	List<TicketMetric> result = tfa.getMetrics();
	EngineerFiles.TICKET_FLOW_REPORT.writeCSV(result, TicketMetric.class);
	successfulFileCreation(EngineerFiles.TICKET_FLOW_REPORT);
    }

}
