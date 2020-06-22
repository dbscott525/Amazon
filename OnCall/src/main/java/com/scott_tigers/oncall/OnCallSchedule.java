package com.scott_tigers.oncall;

import java.util.Date;
import java.util.List;

public class OnCallSchedule extends Schedule {

    public OnCallSchedule(List<Engineer> candidateSchedule, Date startDate, int rotationSize) {
	super(candidateSchedule, startDate, rotationSize);
    }
}
