package com.scott_tigers.oncall.shared;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WeekDays {

    private String currentDate;
    private int numberOfDays;
    private int dayCount;

    public WeekDays(String startDate, int numberOfWeeks) {
	currentDate = Dates.TT_SEARCH.convertFormat(startDate, Dates.SORTABLE);
	numberOfDays = numberOfWeeks * 5;
	dayCount = 0;
    }

    public Stream<String> stream() {
	return StreamSupport
		.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
    }

    public Iterator<String> iterator() {
	return new Iterator<String>() {

	    @Override
	    public boolean hasNext() {
		return dayCount < numberOfDays;
	    }

	    @Override
	    public String next() {
		String nextDate = currentDate;
		currentDate = Dates.SORTABLE.getNextWeekDay(currentDate);
		dayCount++;
		return nextDate;
	    }
	};
    }

}
