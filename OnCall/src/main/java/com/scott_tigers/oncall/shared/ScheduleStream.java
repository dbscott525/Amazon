package com.scott_tigers.oncall.shared;

import java.util.function.Function;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;

public class ScheduleStream {
    private EngineerType oncall;
    private String startDate;
    private Function<String, String> endComputer = date -> date;
    private String endDate;
    private OnlineScheduleEvent nextEvent;

    public void type(EngineerType oncall) {
	this.oncall = oncall;
    }

    public ScheduleStream startDate(String startDate) {
	this.startDate = startDate;
	return this;
    }

    public ScheduleStream days(int days) {
	endComputer = date -> Dates.SORTABLE.getFormattedDelta(startDate, days);
	return this;
    }

    public Stream<OnlineScheduleEvent> getStream() {
	nextEvent = oncall.getEvent(startDate);
	endDate = endComputer.apply(startDate);

	return new IteratorStream<OnlineScheduleEvent>() {

	    @Override
	    protected OnlineScheduleEvent next() {
		OnlineScheduleEvent currentEvent = nextEvent;
		nextEvent = nextEvent.getNextEvent();
		return currentEvent;
	    }

	    @Override
	    protected boolean hasNext() {
		return nextEvent.isInRange(endDate);
	    }

	}.getStream();
    }

}