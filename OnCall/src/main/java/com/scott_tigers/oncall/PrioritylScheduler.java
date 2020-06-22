package com.scott_tigers.oncall;

import java.util.Date;
import java.util.List;

public class PrioritylScheduler extends Scheduler {

    public PrioritylScheduler(Date startDate, Engineer[] engineers) {
	super(startDate, engineers);
    }

    @Override
    protected Schedule createSchedule(List<Engineer> candidateSchedule) {
	return new OnCallSchedule(candidateSchedule, startDate, getRotationSize());
    }

    @Override
    protected int getRotationSize() {
	return Constants.PRIORITY_ENGINEERS_PER_WEEK;
    }

}
