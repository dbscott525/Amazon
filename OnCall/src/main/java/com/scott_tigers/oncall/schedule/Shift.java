package com.scott_tigers.oncall.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.Expertise;

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

	updateUids();
    }

    private void updateUids() {
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

    public List<Engineer> getEngineers() {
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

    public boolean isBefore(String startDate) {
	return startDate.compareTo(date) > 0;
    }

    public boolean isAfter(String startDate) {
	return !isBefore(startDate);
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

    public void setEngineers(List<Engineer> engineers) {
	this.engineers = engineers;
	uids = engineers.stream().map(Engineer::getUid).collect(Collectors.toList());
    }

    public Collection<Engineer> getEngineers(int shiftSize) {
	Optional<Engineer> serverless = engineers
		.stream()
		.filter(eng -> eng.getExpertise().contentEquals(Expertise.Serverless.toString()))
		.findFirst();
	if (serverless.isPresent()) {
	    Engineer serverlessEng = serverless.get();
	    ArrayList<Engineer> newlist = new ArrayList<Engineer>(engineers);
	    newlist.remove(serverlessEng);
	    while (newlist.size() < shiftSize - 1) {
		newlist.add(new Engineer());
	    }
	    newlist.add(serverlessEng);
	    return newlist;
	}
	return engineers;
    }

}
