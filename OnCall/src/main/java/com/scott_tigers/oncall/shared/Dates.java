package com.scott_tigers.oncall.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public enum Dates {
    TIME_STAMP("yyyy-MM-dd-HH-mm-ss"),
    SORTABLE("yyyy-MM-dd"),
    LTTR_URL("yyyy-ww"),
    TT_SEARCH("M/d/y"),
    NICE("MMMM d, yyyy"),
    ONLINE_SCHEDULE("M/d/yy"),
    ICS("yyyyMMdd"),
    DAY_OF_WEEK("EEEE, MMMM d, yyyy"),
    YEAR_MONTH("yyyy-MM"),
    TT_DATE("yyyy-MM-dd hh:mm:ssaa");

    private static final int DAYS_PER_WEEK = 7;
    private String format;

    Dates(String format) {
	this.format = format;
    }

    public String getFormattedDate() {
	return new SimpleDateFormat(format).format(new Date());
    }

    public Date getDateFromString(String dateString) {
	try {
	    return new SimpleDateFormat(format, Locale.getDefault()).parse(dateString);
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
	return getFormattedString(getDateDelta(getDateFromString(dateString), delta));
    }

    public static Date getNextMondayDate() {
	return Date
		.from(LocalDate.now()
			.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant());
    }

    public String getNextMondayFormattedDate() {
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

    public String getCompletedShiftMonday() {
	String monday = getNextMondayFormattedDate();
	long mondayTime = getDateFromString(monday).getTime();
	long nowTime = getDateFromString(getFormattedString()).getTime();
	long daysDelta = (mondayTime - nowTime) / 60 / 60 / 24 / 1000;
	return daysDelta > 3 ? getFormattedDelta(monday, -7) : monday;
    }

    public static Date addMonth(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.MONTH, 1);
	return calendar.getTime();
    }

    public String getClosestMonday() {
	String nextMonday = getNextMondayFormattedDate();
	long nextMondayTime = getTime(nextMonday);

	String lastMonday = getFormattedDelta(nextMonday, -DAYS_PER_WEEK);
	long lastMondayTime = getTime(lastMonday);

	long nowTime = new Date().getTime();

	long nextMondayDelta = nextMondayTime - nowTime;
	long lastMondayDelta = nowTime - lastMondayTime;

	return nextMondayDelta < lastMondayDelta ? nextMonday : lastMonday;
    }

    long getTime(String date) {
	return getDateFromString(date).getTime();
    }

    public String addMonths(String stringDate, int months) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(getDateFromString(stringDate));
	calendar.add(Calendar.MONTH, months);
	return getFormattedString(calendar.getTime());
    }

    public String getLastMondayFormattedDate() {
	return getFormattedDelta(getNextMondayFormattedDate(), -DAYS_PER_WEEK);
    }

    public String getFirstDayOfWeek(String date) {
	return getFormattedDate(date, ld -> ld.with(DayOfWeek.MONDAY));
    }

    public String getFirstDayOfMonth(String date) {
	return getFormattedDate(date, ld -> ld.withDayOfMonth(1));
    }

    private String getFormattedDate(String date, Function<LocalDate, LocalDate> supplier) {

	LocalDate localDate = supplier.apply(
		getDateFromString(date)
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate());

	return getFormattedString(
		Date.from(localDate.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));
    }

    public int getDayOfWeek(String dateString) {
	Date date = getDateFromString(dateString);
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	return dayOfWeek;
    }

    public int getDifference(String start, String end) {
	long milliDiff = getDateFromString(end).getTime() - getDateFromString(start).getTime();
	return (int) TimeUnit.DAYS.convert(milliDiff, TimeUnit.MILLISECONDS);
    }

    public String getLastDayOfLastFullWeek() {
	Calendar date = Calendar.getInstance();
	date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	return Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(date.getTime()), -8);
    }

    public boolean isWeekend(String date) {
	switch (getDayOfWeek(date)) {
	case Calendar.SATURDAY:
	case Calendar.SUNDAY:
	    return true;
	default:
	    return false;
	}
    }

}
