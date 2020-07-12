package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.Date;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.Util;

public class LaunchEngineTicketsForDailyReview {

    public static void main(String[] args) {
	new LaunchEngineTicketsForDailyReview().run();
    }

    private void run() {
	int delta = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 ? -3 : -1;
	Util.launchURL(
		"https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
			+ Dates.TT_SEARCH.getFormattedString(Dates.getDateDelta(new Date(), delta))
			+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21");

    }

}
