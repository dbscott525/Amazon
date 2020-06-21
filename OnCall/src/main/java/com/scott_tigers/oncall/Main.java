package com.scott_tigers.oncall;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

// TODO: first and second tier
// TODO: buddies
// TODO escalation schedule
// TODO take into account previous schedules

public class Main {

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
	Engineer[] engineers = new Engineers().getEngineers().toArray(Engineer[]::new);
	OnCallScheduler onCallSchedule = new OnCallScheduler(startDate, engineers);
	System.out.println("onCallSchedule.getOnCallSchedule()=" + (onCallSchedule.getOnCallSchedule()));
    }

}
