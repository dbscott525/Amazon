package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.schedule.Scheduler;
import com.scott_tigers.oncall.shared.Dates;

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

    private static final int ROTATION_DELTA = 2;
    private static final int SHIFT_SIZE = 5;
    private static final int WEEKS_BETWEEN_SHIFTS = 2;
    private static final int MINUTE_TIME_LIMIT = 5;
    private static final String NEW_SCHEDULE_START_DATE = "2020-11-23";
    private static final double MINIUM_STANDARD_DEVIATION = .6;
    private static final int SHIFT_FREQUENCY = 7;
    private static final int TEAM_SIZE = 6;
    private static final int SHIFTS_PER_RUN = 2;
    private static final int RUNS = 5;

    public static void main(String[] args) throws IOException {
	new CreateCITSchedule().run();
    }

    private void run() {
	IntStream.range(0, RUNS).forEach(runNumber -> {
	    String scheduleStart = Dates.SORTABLE.getFormattedDelta(NEW_SCHEDULE_START_DATE,
		    runNumber * SHIFTS_PER_RUN * 7);
	    new Scheduler()
		    .searchByRandom()
		    .teamSize(TEAM_SIZE)
		    .shiftSize(SHIFT_SIZE)
		    .shiftFrequency(SHIFT_FREQUENCY)
		    .minimumStandardDeviation(MINIUM_STANDARD_DEVIATION)
		    .timeLimit(MINUTE_TIME_LIMIT)
		    .newScheduleStart(scheduleStart)
		    .weeksBetweenShift(WEEKS_BETWEEN_SHIFTS)
		    .rotationDelta(ROTATION_DELTA)
		    .shifts(SHIFTS_PER_RUN)
		    .run()
		    .save();

	});
    }

}
