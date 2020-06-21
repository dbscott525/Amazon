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
	Date date1 = new Date();
	try {
	    date1 = new SimpleDateFormat("M/d/y").parse(args[0]);
	} catch (ParseException e) {
	    System.out.println(args[0] + " is and invalid date");
	    System.exit(1);
	}
	List<Engineer> engineers = new Engineers().getEngineers();
	System.out.println("engineers=" + (engineers));
	var schedule = new OptimalSchedule(engineers, date1).getSchedule();
	System.out.println("schedule=" + (schedule));
    }

}
