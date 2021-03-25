package com.amazon.amsoperations.shared;

import com.amazon.amsoperations.bean.Engineer;

public class ScheduleType {

    private TimeZone timeZone;
    private int hours;

    public ScheduleType(TimeZone timeZone, int hours) {
	this.timeZone = timeZone;
	this.hours = hours;
    }

    public TimeZone getTimeZone() {
	return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
	this.timeZone = timeZone;
    }

    public int getHours() {
	return hours;
    }

    public void setHours(int hours) {
	this.hours = hours;
    }

    @Override
    public String toString() {
	return "ScheduleType [timeZone=" + timeZone + ", hours=" + hours + "]";
    }

    public boolean in(Engineer eng) {
	return eng.getTimeZone().equals(timeZone.toString());
    }

}
