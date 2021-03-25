package com.scott_tigers.oncall.shared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ScheduleTypeIterator implements Iterator<ScheduleType> {

    private List<ScheduleType> scheduleTypes = new ArrayList<>();
    private Iterator<ScheduleType> listIterator;

    public ScheduleTypeIterator(Consumer<ScheduleTypeIterator> adder) {
	adder.accept(this);
    }

    public ScheduleTypeIterator add(TimeZone timeZone, int hours) {
	scheduleTypes.add(new ScheduleType(timeZone, hours));
	return this;
    }

    @Override
    public boolean hasNext() {
	return true;
    }

    @Override
    public ScheduleType next() {
	while (listIterator == null || !listIterator.hasNext()) {
	    listIterator = scheduleTypes.iterator();
	}
	return listIterator.next();
    }

    public List<ScheduleType> getScheduleTypes() {
	return scheduleTypes;
    }
}
