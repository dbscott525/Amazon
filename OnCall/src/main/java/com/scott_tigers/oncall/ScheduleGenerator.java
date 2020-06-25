package com.scott_tigers.oncall;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */

/**
 * (Put description here)
 * 
 * @author bruscob
 */

public class ScheduleGenerator {

    public static void main(String[] args) throws IOException {
	if (args.length == 0) {
	    System.out.println("Usage: OnCall <start-date>");
	    System.exit(1);
	}

	Date startDate = new Date();
	try {
	    startDate = new SimpleDateFormat("M/d/y").parse(args[0]);
	} catch (ParseException e) {
	    System.out.println(args[0] + " is and invalid date");
	    System.exit(1);
	}
	List<Engineer> engineers = new Engineers().getEngineers();
	System.out.println("engineers=" + (engineers));

	ScheduleType scheduleType = ScheduleType.ON_CALL;
//	ScheduleType scheduleType = ScheduleType.PRIORITY;
	System.out.println("startDate=" + (startDate));
	System.out.println("engineers=" + (engineers));
	scheduleType.build()
		.searchByRandom()
		.startDate(startDate)
		.engineers(engineers)
		.passes(2)
		.timeLimit(10)
		.run()
		.writeToCSV(Constants.ENGINEERS_SCHEDULE_FILE);
    }
}
