package com.scott_tigers.oncall.test;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    private static final boolean TEST_DATA = true;

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	Date date = Dates.ONLINE_SCHEDULE.getDateFromString("12/25/21 10:00");
	System.out.println("date=" + (date));

    }

    @SuppressWarnings("unused")
    private void run21() throws Exception {
	Calendar cld = Calendar.getInstance();
	cld.set(Calendar.YEAR, 2020);
	cld.set(Calendar.WEEK_OF_YEAR, 35);
	Date result = cld.getTime();
	System.out.println("result=" + (result));
    }

}