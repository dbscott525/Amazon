package com.scott_tigers.oncall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class DateConflictDetector {

    private Date startDate;
    private List<List<Engineer>> schedule;
    private int daysBetweenShifts;
    private int days;

    public boolean hasConflict() {
	return IntStream
		.range(0, schedule.size())
		.anyMatch(scheduleNumber -> {
		    int dayNumber = scheduleNumber * daysBetweenShifts;
		    String dateString = Util.getDateIncrementString(startDate, dayNumber,
			    Constants.SORTABLE_DATE_FORMAT);
		    return schedule
			    .get(scheduleNumber)
			    .stream().anyMatch(eng -> eng.hasDateConflict(dateString));
		});
    }

    public DateConflictDetector startDate(Date startDate) {
	this.startDate = startDate;
	return this;
    }

    public DateConflictDetector schedule(List<List<Engineer>> schedule) {
	this.schedule = schedule;
	return this;
    }

    public DateConflictDetector daysBetweenShifts(int daysBetweenShifts) {
	this.daysBetweenShifts = daysBetweenShifts;
	return this;
    }

    public DateConflictDetector engineer(Engineer engineer) {
	schedule = new ArrayList<List<Engineer>>() {
	    private static final long serialVersionUID = 1L;
	    {
		add(new ArrayList<Engineer>() {
		    private static final long serialVersionUID = 1L;
		    {
			add(engineer);
		    }
		});

	    }
	};
	return this;
    }

    public DateConflictDetector days(int days) {
	this.days = days;
	// TODO Auto-generated method stub
	return null;
    }

}
