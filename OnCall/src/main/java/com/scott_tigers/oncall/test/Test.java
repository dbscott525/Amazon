package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) {
	String testDate = "7/29/2020";
	System.out.println("testDate=" + (testDate));
	System.out
		.println("Dates.LTTR_URL.getDateFromString(testDate)="
			+ (Dates.ONLINE_SCHEDULE.getDateFromString(testDate)));
    }

}