package com.scott_tigers.oncall.utility;

import java.io.IOException;

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
	new ScheduleGenerator().run();
    }

    private void run() {
	new Scheduler()
		.searchByRandom()
		.teamSize(6)
//		.miniumPercentile(0)
		.shiftSize(5)
		.shiftFrequency(7)
		.minimumStandardDeviation(.05)
//		.startDate(getStartDate())
//		.engineers(engineers)
		.passes(1)
		.timeLimit(20)
		.run()
		.save(EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE);
//	.save(EngineerFiles.NEW_SCHEDULE);
    }

}
