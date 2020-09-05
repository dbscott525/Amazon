package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	String monday = Dates.SORTABLE.getNextMondayFormattedDate();
//	System.out.println("monday=" + (monday));
	long mondayTime = Dates.SORTABLE.getDateFromString(monday).getTime();
	System.out.println("Dates.getNextMondayDatex()=" + (Dates.getNextMondayDate()));
	long now = Dates.SORTABLE.getDateFromString(Dates.SORTABLE.getFormattedString()).getTime();
	System.out.println("now=" + (now));
	long daysDelta = (mondayTime - now) / 60 / 60 / 24 / 1000;
	System.out.println("daysDelta=" + (daysDelta));

	if (daysDelta >= 3) {
	    monday = Dates.SORTABLE.getFormattedDelta(monday, -7);
	}

    }
}