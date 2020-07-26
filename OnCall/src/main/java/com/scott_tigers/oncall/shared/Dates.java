package com.scott_tigers.oncall.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public enum Dates {
    TIME_STAMP("yyyy-MM-dd-HH-mm-ss"),
    SORTABLE("yyyy-MM-dd"),
    LTTR_URL("yyyy-ww"),
    TT_SEARCH("M/d/y"),
    NICE("MMMM d, yyyy"),
    ONLINE_SCHEDULE("M/d/yy");

    private String format;

    Dates(String format) {
	this.format = format;
    }

    String getFormattedDate() {
	return new SimpleDateFormat(format).format(new Date());
    }

    public Date getDateFromString(String dateString) {
	try {
	    return new SimpleDateFormat(format).parse(dateString);
	} catch (ParseException e) {
	    return new Date();
	}
    }

    public static Date getDateDelta(Date date, int delta) {
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, delta);
	return c.getTime();
    }

    public static Date getWeekDelta(Date date, int delta) {
	return getDateDelta(date, delta * 7);
    }

    public String getFormattedString(Date date) {
	return new SimpleDateFormat(format).format(date);
    }

    public String getFormattedString() {
	return getFormattedString(new Date());
    }

    public String getFormattedDelta(String dateString, int delta) {
	Date d1 = getDateFromString(dateString);
	Date d2 = getDateDelta(d1, delta);
	String d3 = getFormattedString(d2);
	return d3;
    }

    public static Date getNextMondayDatex() {
	return Date
		.from(LocalDate.now()
			.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant());
    }

    public String getNextMondayDate() {
	return getFormattedString(Date
		.from(LocalDate.now()
			.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()));
    }

    String getNextWeekDay(String dateString) {
	Date date = getDateFromString(dateString);
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
	int increment;
	switch (dayOfTheWeek) {
	case 7:
	    increment = 2;
	    break;
	case 6:
	    increment = 3;
	    break;
	default:
	    increment = 1;
	    break;
	}

	return getFormattedDelta(dateString, increment);
    }

    public String convertFormat(String date, Dates toFormat) {
	return toFormat.getFormattedString(getDateFromString(date));
    }

}
