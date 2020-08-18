package com.scott_tigers.oncall.utility;

import java.util.Date;
import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Dates;

public class LaunchAMSLTTRGraph extends Utility {

    private static final String TOP_TICKETS = "https://rds-portal.corp.amazon.com/lttr/reports/prioritization?week=START&range=END&team=Aurora+MySQL+-+Engine#";
    private static final String TICKET_GRAPH = "https://rds-portal.corp.amazon.com/lttr/reports/weekly?week=START&range=END&team=Aurora+MySQL+-+Engine&graph=Line";
    private static final int NUMBER_OF_WEEKS_IN_REPORT = 6;

    public static void main(String[] args) throws Exception {
	new LaunchAMSLTTRGraph().run();
    }

    private String end;
    private String start;

    private void run() throws Exception {
	Date nowDate = new Date();
	end = Dates.LTTR_URL.getFormattedString(nowDate);
	System.out.println("end=" + (end));
	start = Dates.LTTR_URL.getFormattedString(Dates.getWeekDelta(nowDate, -NUMBER_OF_WEEKS_IN_REPORT));
	System.out.println("start=" + (start));

	Stream.of(TICKET_GRAPH, TOP_TICKETS)
		.map(this::injectDates)
		.forEachOrdered(this::launchUrl);
    }

    private String injectDates(String urlTemplate) {
	return urlTemplate.replaceAll("(.*)START(.*)END(.*)", "$1" + start + "$2" + end + "$3");
    }

}
