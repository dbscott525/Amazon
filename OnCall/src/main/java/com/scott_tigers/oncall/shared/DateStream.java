package com.scott_tigers.oncall.shared;

import java.util.stream.Stream;

public class DateStream {

    public static Stream<String> get(String startDate, int days) {
	return get(startDate, Dates.SORTABLE.getFormattedDelta(startDate, days - 1), 1);
    }

    public static Stream<String> get(String startDate, String endDate) {
	return get(startDate, endDate, 1);
    }

    public static Stream<String> get(String startDate, String endDate, int delta) {
	return new IteratorStream<String>() {
	    String currentDate = startDate;

	    @Override
	    protected boolean hasNext() {
		return currentDate.compareTo(endDate) <= 0;
	    }

	    @Override
	    protected String next() {
		String nextDate = currentDate;
		currentDate = Dates.SORTABLE.getFormattedDelta(currentDate, delta);
		return nextDate;
	    }

	}.getStream();

    }
}
