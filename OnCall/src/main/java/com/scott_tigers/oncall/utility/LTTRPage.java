package com.scott_tigers.oncall.utility;

import java.util.Date;
import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Dates;

public enum LTTRPage {
    TOP("https://rds-portal.corp.amazon.com/lttr/reports/prioritization?week=START&range=END&team=Aurora+MySQL+-+Engine#"),
    GRAPH("https://rds-portal.corp.amazon.com/lttr/reports/weekly?week=START&range=END&team=Aurora+MySQL+-+Engine&graph=Line");

    private static final int NUMBER_OF_WEEKS_IN_REPORT = 6;

    private String urlTemplate;

    LTTRPage(String urlTemplate) {
	this.urlTemplate = urlTemplate;
    }

    static Stream<LTTRPage> stream() {
	return Stream.of(values());
    }

    public String getUrl() {
	Date nowDate = new Date();
	String end = Dates.LTTR_URL.getFormattedString(nowDate);
	String start = Dates.LTTR_URL.getFormattedString(Dates.getWeekDelta(nowDate, -NUMBER_OF_WEEKS_IN_REPORT));
	return urlTemplate.replaceAll("(.*)START(.*)END(.*)", "$1" + start + "$2" + end + "$3");
    }

}
