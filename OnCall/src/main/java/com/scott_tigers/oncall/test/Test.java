package com.scott_tigers.oncall.test;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	Stream.of("2020-07-28", "2020-08-05").forEach(input -> {
	    String d4 = Dates.SORTABLE.getDateFromString(input).toInstant()
		    .atZone(ZoneId.systemDefault())
		    .toLocalDate().with(DayOfWeek.MONDAY).toString();

	});
    }

}