package com.scott_tigers.oncall.bean;

import com.scott_tigers.oncall.shared.Dates;

public class OnCallScheduleRow {

    String date;
    String uid;
    String shift;
    String type;

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

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public void setUid(String uid) {
	this.uid = uid;
    }

    public void setShift(String shift) {
	this.shift = shift;
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
