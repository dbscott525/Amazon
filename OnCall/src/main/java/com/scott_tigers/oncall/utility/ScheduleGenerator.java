package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.schedule.Engineers;
import com.scott_tigers.oncall.schedule.Scheduler;
import com.scott_tigers.oncall.shared.EngineerFiles;

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

	System.out.println("startDate=" + (startDate));
	System.out.println("engineers=" + (engineers));
	new Scheduler()
		.searchByRandom()
		.teamSize(6)
//		.miniumPercentile(0)
		.shiftSize(5)
		.shiftFrequency(7)
		.minimumStandardDeviation(.1)
		.startDate(startDate)
		.engineers(engineers)
		.passes(1)
		.timeLimit(20)
		.run()
		.save(EngineerFiles.NEW_SCHEDULE);

    }
}
