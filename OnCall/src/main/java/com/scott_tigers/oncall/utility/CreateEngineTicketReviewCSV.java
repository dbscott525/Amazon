package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.Status;

public class CreateEngineTicketReviewCSV extends Utility {

    private static final List<String> ENGINE_REVIEW_COLUMNS = Arrays.asList(
	    Properties.LINK,
	    Properties.COMMENTS,
	    Properties.REVIEW,
	    Properties.STATUS,
	    Properties.ROOT_CAUSE_DETAILS,
	    Properties.DESCRIPTION);

    public static void main(String[] args) throws Exception {
	new CreateEngineTicketReviewCSV().run();
    }

    private void run() throws Exception {
	EngineerFiles.ENGINE_TICKET_DAILY_REVIEW
		.writeCSV(getTicketStreamFromUrl(getURL())
//			.filter(tt -> !tt.getStatus().equals(TicketStatuses.PENDING_PENDING_ROOT_CAUSE))
			.filter(tt -> Status.get(tt.getStatus()).includeInSummary())
			.collect(Collectors.toList()),
			ENGINE_REVIEW_COLUMNS);

	successfulFileCreation(EngineerFiles.ENGINE_TICKET_DAILY_REVIEW);
    }

    private String getURL() {
	int delta = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 ? -3 : -1;
	return "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ Dates.TT_SEARCH.getFormattedString(Dates.getDateDelta(new Date(), delta))
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21";
    }

}
