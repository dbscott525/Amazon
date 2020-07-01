package com.scott_tigers.oncall.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum Dates {
    TIME_STAMP("yyyy-MM-dd-HH-mm-ss"), SORTABLE_DAY_DATE("yyyy-MM-dd"), LTTR_URL("yyyy-ww"), TT_SEARCH("M/d/y");

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

}
