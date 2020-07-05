package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.Date;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.Util;

public class LaunchCitTicketsResolved {

    public static void main(String[] args) {
	new LaunchCitTicketsResolved().run();
    }

    private void run() {

	Calendar today = Calendar.getInstance();
	int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

	int delta = dayOfWeek == 7 ? -5 : -(dayOfWeek + 5);

	Date startDate = Dates.getDateDelta(today.getTime(), delta);
	String startString = Dates.TT_SEARCH.getFormattedString(startDate);
	Date endDate = Dates.getDateDelta(startDate, 5);
	String endString = Dates.TT_SEARCH.getFormattedString(endDate);
	String searchString = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Pending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date="
		+ startString
		+ "%2C"
		+ endString
		+ "&tags=&case_type=&building_id=&search=Search%21";

	Util.launchURL(searchString);

    }

}
