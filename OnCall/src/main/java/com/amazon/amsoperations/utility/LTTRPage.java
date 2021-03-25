package com.amazon.amsoperations.utility;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.amazon.amsoperations.bean.LTTRTicket;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.URL;
import com.amazon.amsoperations.shared.Util;
import com.amazon.amsoperations.shared.WebElements;

public enum LTTRPage {
    TOP(URL.LTTR_PRIORITY_RANGE),
    GRAPH("https://rds-portal.corp.amazon.com/lttr/reports/weekly?week=START&range=END&team=Aurora+MySQL+-+Engine&graph=Line"),
    LAST_FULL_WEEK(URL.LTTR_PRIORITY_RANGE) {
	@Override
	protected int getNumberOfWeeks() {
	    return 1;
	}

    },
    PENULTIMATE_FULL_WEEK(URL.LTTR_PRIORITY_RANGE) {
	@Override
	protected int getLastWeekDelta() {
	    return 2;
	}

	@Override
	protected int getNumberOfWeeks() {
	    return 1;
	}

    };

    private static final int NUMBER_OF_WEEKS_IN_REPORT = 4;

    private String urlTemplate;

    LTTRPage(String urlTemplate) {
	this.urlTemplate = urlTemplate;
    }

    static Stream<LTTRPage> stream() {
	return Stream.of(values());
    }

    public String getUrl() {
	boolean GET_WEEKS_FROM_PAGE = true;
	if (GET_WEEKS_FROM_PAGE) {
	    List<String> weeks = LTTRWeeks.get();
	    String startWeek = weeks.get(getNumberOfWeeks() + getLastWeekDelta() - 1);
	    String endWeek = weeks.get(getLastWeekDelta());
	    String url = urlTemplate.replaceAll("(.*)START(.*)END(.*)", "$1" + startWeek + "$2" + endWeek + "$3");
	    return url;
	} else {
	    Date nowDate = new Date();
	    System.out.println("nowDate=" + (nowDate));
	    System.out.println("getLastWeekDelta()=" + (getLastWeekDelta()));
	    Date endDate = Dates.getWeekDelta(nowDate, -getLastWeekDelta());
	    System.out.println("endDate=" + (endDate));

	    String end = fixWeekOf(Dates.LTTR_URL.getFormattedString(endDate));
	    System.out.println("end=" + (end));

	    String start = fixWeekOf(Dates.LTTR_URL
		    .getFormattedString(Dates
			    .getWeekDelta(
				    nowDate,
				    -(getNumberOfWeeks() + getLastWeekDelta()))));

	    return urlTemplate.replaceAll("(.*)START(.*)END(.*)", "$1" + start + "$2" + end + "$3");

	}
    }

    String fixWeekOf(String date) {
	return date.compareTo("2020-01") == 0 ? "2021-01" : date;
    }

    protected int getNumberOfWeeks() {
	return NUMBER_OF_WEEKS_IN_REPORT;
    }

    protected int getLastWeekDelta() {
	return 1;
    }

    Stream<LTTRTicket> getLttrTicketStream(WebDriver driver) {
	driver.get(getUrl());
	System.out.println("page loaded");
	Util.sleep(2);
	return driver
		.findElement(By.id(WebElements.TBL_PRIORITIZATION_ID))
		.findElements(By.tagName(WebElements.TABLE_ROW_TAG))
		.stream()
		.map(LTTRTicket::new)
		.filter(LTTRTicket::validTicket);
    }

    Map<String, LTTRTicket> getMap() {
	LTTRWeeks.get();
	WebDriver driver = Util.getWebDriver();
	System.out.println("this=" + (this));
	Map<String, LTTRTicket> map = getLttrTicketStream(driver)
		.collect(Collectors.toMap(LTTRTicket::getTicket, Function.identity()));
	driver.close();
	return map;
    }
}
