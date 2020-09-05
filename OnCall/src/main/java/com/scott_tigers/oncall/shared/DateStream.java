package com.scott_tigers.oncall.shared;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DateStream {

    public static Stream<String> getStream(String startDate, int days) {
	return getStream(startDate, Dates.SORTABLE.getFormattedDelta(startDate, days - 1), 1);
    }

    public static Stream<String> getStream(String startDate, String endDate, int delta) {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<String>() {
			    String currentDate = startDate;

			    @Override
			    public boolean hasNext() {
				return currentDate.compareTo(endDate) <= 0;
			    }

			    @Override
			    public String next() {
				String nextDate = currentDate;
				currentDate = Dates.SORTABLE.getFormattedDelta(currentDate, delta);
				return nextDate;
			    }
			},
			Spliterator.ORDERED),
		false);
    }

}
