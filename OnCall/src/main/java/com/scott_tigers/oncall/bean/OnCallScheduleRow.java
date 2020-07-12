package com.scott_tigers.oncall.bean;

import com.scott_tigers.oncall.shared.Dates;

public class OnCallScheduleRow {

    String date;
    String uid;
    String shift;

    public OnCallScheduleRow(String date, String uid) {

	this.uid = uid;
	this.date = date;
    }

    public OnCallScheduleRow() {
    }

    public String getDate() {
	return date;
    }

    public String getUid() {
	return uid;
    }

    public String getShift() {
	return shift;
    }

    public OnCallScheduleRow canonicalDate() {
	date = Dates.SORTABLE.getFormattedString(Dates.ONLINE_SCHEDULE.getDateFromString(date));
	return this;
    }

    @Override
    public String toString() {
	return "OnCallScheduleRow [date=" + date + ", uid=" + uid + "]";
    }

    public OnCallScheduleRow adjustDate() {
	date = Dates.SORTABLE.getFormattedString(Dates.getDateDelta(Dates.SORTABLE.getDateFromString(date), -1));
	return this;
    }

    public boolean afterToday() {
	return date.compareTo(Dates.SORTABLE.getFormattedString()) > 0;
    }

}
