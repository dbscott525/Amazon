package com.scott_tigers.oncall;

import java.util.Date;

public class OnCallScheduler {

    private Date startDate;
    private OnCallSchedule onCallSchedule;

    public OnCallScheduler(Date startDate, Engineer[] engineers) {
	this.startDate = startDate;
	var slots = engineers.length / Constants.ON_CALLS_PER_DAY * Constants.ON_CALLS_PER_DAY;
	new ConbinationFinder(engineers, candidateSchedule -> processCandidateSchedule(candidateSchedule), slots);
    }

    private void processCandidateSchedule(Engineer[] candidateSchedule) {
	onCallSchedule = new OnCallSchedule(candidateSchedule, startDate).getBestSchedule(onCallSchedule);
    }

    public OnCallSchedule getOnCallSchedule() {
	return onCallSchedule;
    }

}
