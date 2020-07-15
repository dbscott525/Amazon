package com.scott_tigers.oncall.utility;

import java.util.Date;

import com.scott_tigers.oncall.shared.Dates;

public class LaunchAMSLTTRGraph extends Utility {

    private static final int NUMBER_OF_WEEKS_IN_REPORT = 6;

    public static void main(String[] args) throws Exception {
	new LaunchAMSLTTRGraph().run();
    }

    private void run() throws Exception {
	Date nowDate = new Date();
	String now = Dates.LTTR_URL.getFormattedString(nowDate);
	String weeksAgo = Dates.LTTR_URL.getFormattedString(Dates.getWeekDelta(nowDate, -NUMBER_OF_WEEKS_IN_REPORT));

	String url = "https://rds-portal.corp.amazon.com/lttr/reports/weekly?week="
		+ weeksAgo
		+ "&range="
		+ now
		+ "&team=Aurora+MySQL+-+Engine&graph=Line";

	launchUrl(url);
    }

}
