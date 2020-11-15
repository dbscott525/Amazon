package com.scott_tigers.oncall.utility;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.URL;
import com.scott_tigers.oncall.shared.Util;
import com.scott_tigers.oncall.shared.WebElements;

public enum LTTRPage {
    TOP(URL.LTTR_PRIORITY),
    GRAPH("https://rds-portal.corp.amazon.com/lttr/reports/weekly?week=START&range=END&team=Aurora+MySQL+-+Engine&graph=Line"),
    LAST_FULL_WEEK(URL.LTTR_PRIORITY) {
	@Override
	protected int getNumberOfWeeks() {
	    return 0;
	}

    },
    PENULTIMATE_FULL_WEEK(URL.LTTR_PRIORITY) {
	@Override
	protected int getLastWeekDelta() {
	    return 2;
	}

	@Override
	protected int getNumberOfWeeks() {
	    return 0;
	}

    };

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
	String end = Dates.LTTR_URL.getFormattedString(Dates.getWeekDelta(nowDate, -getLastWeekDelta()));
	String start = Dates.LTTR_URL.getFormattedString(Dates.getWeekDelta(nowDate, -(getNumberOfWeeks() + 1)));
	return urlTemplate.replaceAll("(.*)START(.*)END(.*)", "$1" + start + "$2" + end + "$3");
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

    Map<String, LTTRTicket> getMap(WebDriver driver) {
	return getLttrTicketStream(driver)
		.collect(Collectors.toMap(LTTRTicket::getTicket, Function.identity()));
    }

}
