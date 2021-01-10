package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public OnCallScheduleRow(String type) {
	this.type = type;
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
//	System.out.println("original date=" + (date));
//	date = Dates.SORTABLE.getFormattedString(Dates.ONLINE_SCHEDULE.getDateFromString(date));
//	System.out.println("fixed date=" + (date));
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((date == null) ? 0 : date.hashCode());
	result = prime * result + ((uid == null) ? 0 : uid.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	OnCallScheduleRow other = (OnCallScheduleRow) obj;
	if (date == null) {
	    if (other.date != null)
		return false;
	} else if (!date.equals(other.date))
	    return false;
	if (uid == null) {
	    if (other.uid != null)
		return false;
	} else if (!uid.equals(other.uid))
	    return false;
	return true;
    }

    @JsonIgnore
    public boolean isCurrent() {
	return Dates.SORTABLE.getFormattedString().compareTo(date) <= 0;
    }

    public boolean before(String date) {
	System.out.println("date=" + (date));
	return date.compareTo(this.date) > 0;
    }

}
