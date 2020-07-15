package com.scott_tigers.oncall.utility;

import java.util.Comparator;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class LaunchCandidateRootCauseTickets extends Utility {

    public static void main(String[] args) throws Exception {

	new LaunchCandidateRootCauseTickets().run();

    }

    private void run() throws Exception {
	String url = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ Dates.SORTABLE
			.convertFormat(Dates.SORTABLE
				.getFormattedDelta(EngineerFiles.ROOT_CAUSE_TO_DO
					.readCSVToPojo(TT.class)
					.stream()
					.map(TT::getCreateDate)
					.min(Comparator.comparing(String::toString))
					.orElse("000")
					.substring(0, 10),
					-1),
				Dates.TT_SEARCH)
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21";

	getTicketStreamFromUrl(url);
    }

}