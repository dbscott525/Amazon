package com.scott_tigers.oncall;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class Scheduler {

    protected Date startDate;
    private Schedule schedule;
    protected Engineer[] engineers;;

    public Scheduler(Date startDate, Engineer[] engineers) {
	this.startDate = startDate;
	this.engineers = engineers;
	new CombinationFinder<Engineer>()
		.input(Arrays.asList(engineers))
		.resultSize(engineers.length / getRotationSize() * getRotationSize())
		.combinationHandler(this::processCandidateSchedule)
		.generate();
    }

    protected abstract int getRotationSize();

    private void processCandidateSchedule(List<Engineer> candidateSchedule) {
	schedule = createSchedule(candidateSchedule).getBestSchedule(schedule);
    }

    protected abstract Schedule createSchedule(List<Engineer> candidateSchedule);

    public Schedule getSchedule() {
	return schedule;
    }

}
