package com.scott_tigers.oncall.newschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleRow;

public class Shift {

    private transient ScheduleCreator scheduleCreator;
    private transient List<Engineer> engineers;
    private List<String> uids;
    private String date;

    public Shift(ScheduleRow scheduleRow) {
	date = scheduleRow.getDate();
	uids = scheduleRow.getEngineers()
		.stream()
		.map(Engineer::getUid)
		.collect(Collectors.toList());
    }

    public Shift(ScheduleCreator scheduleCreator, String date, List<Engineer> candidateEngineers) {
	this.scheduleCreator = scheduleCreator;
	this.date = date;

	engineers = new ArrayList<>(candidateEngineers);

	uids = engineers.stream()
		.map(Engineer::getUid)
		.collect(Collectors.toList());
    }

    public Double getSumOfLevels() {
	return getEngineers()
		.stream()
		.mapToDouble(Engineer::getLevel)
		.sum();
    }

    List<Engineer> getEngineers() {
	return Optional
		.ofNullable(engineers)
		.orElseGet(() -> {
		    engineers = uids
			    .stream()
			    .map(scheduleCreator::getEngineer)
			    .collect(Collectors.toList());
		    return engineers;
		});
    }

    public boolean before(String startDate) {
	return startDate.compareTo(date) > 0;
    }

    public String getDate() {
	return date;
    }

    public List<String> getUids() {
	return uids;
    }

    public void setScheduleCreator(ScheduleCreator scheduleCreator) {
	this.scheduleCreator = scheduleCreator;
    }

}
