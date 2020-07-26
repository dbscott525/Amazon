package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TicketStatusCount;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCitResolvedTicketsTable extends Utility {

    public static void main(String[] args) throws Exception {
	new CreateCitResolvedTicketsTable().run();
    }

    private void run() throws Exception {

	EngineerFiles.RESOLVED_TICKET_SUMMARY.writeCSV(
		getTicketStreamFromUrl(getUrl())
			.filter(tt -> !tt.getStatus().equals("Pending Pending Root Cause"))
			.collect(Collectors.groupingBy(TT::getStatus))
			.entrySet()
			.stream()
			.map(TicketStatusCount::new)
			.collect(Collectors.toList()),
		TicketStatusCount.class);

	successfulFileCreation(EngineerFiles.RESOLVED_TICKET_SUMMARY);
    }

    private String getUrl() {
	Calendar today = Calendar.getInstance();
	int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
	int delta = dayOfWeek == 1 ? -6 : 2 - dayOfWeek;

	String startString = Dates.TT_SEARCH.getFormattedString(Dates.getDateDelta(today.getTime(), delta));
	String endString = Dates.TT_SEARCH
		.getFormattedString(Dates.getDateDelta(Dates.getDateDelta(today.getTime(), delta), 5));

	return "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Pending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date="
		+ startString
		+ "%2C"
		+ endString
		+ "&tags=&case_type=&building_id=&search=Search%21";
    }

}
