package com.scott_tigers.oncall.utility;

import java.io.IOException;

import com.scott_tigers.oncall.schedule.Scheduler;

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

public class CreateCITSchedule {

    private static final int SHIFT_SIZE = 5;
    private static final int WEEKS_BETWEEN_SHIFTS = 3;
    private static final int MINUTE_TIME_LIMIT = 20;
    private static final String NEW_SCHEDULE_START_DATE = "2020-08-03";
    private static final double MINIUM_STANDARD_DEVIATION = .05;
    private static final int SHIFT_FREQUENCY = 7;
    private static final int TEAM_SIZE = 6;

    public static void main(String[] args) throws IOException {
	new CreateCITSchedule().run();
    }

    private void run() {
	new Scheduler()
		.searchByRandom()
		.teamSize(TEAM_SIZE)
//		.miniumPercentile(0)
		.shiftSize(SHIFT_SIZE)
		.shiftFrequency(SHIFT_FREQUENCY)
		.minimumStandardDeviation(MINIUM_STANDARD_DEVIATION)
		.timeLimit(MINUTE_TIME_LIMIT)
		.newScheduleStart(NEW_SCHEDULE_START_DATE)
		.weeksBetweenShift(WEEKS_BETWEEN_SHIFTS)
		.run()
		.save();
//	.save(EngineerFiles.NEW_SCHEDULE);
    }

}
