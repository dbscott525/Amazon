package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scott_tigers.oncall.shared.Dates;

public class OnCallScheduleRow {

    String startDate;
    String endDate;
    String startTime;
    String uid;
    String shift;
    String type;

    public OnCallScheduleRow(String date, String uid) {

	this.uid = uid;
	this.startDate = date;
    }

    public OnCallScheduleRow() {
    }

    public OnCallScheduleRow(String type) {
	this.type = type;
    }

    public String getStartDate() {
	return startDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public String getUid() {
	return uid;
    }

    public String getShift() {
	return shift;
    }

    public OnCallScheduleRow canonicalDate() {
	return this;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    public String getStartTime() {
	return startTime;
    }

    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    public void setUid(String uid) {
	this.uid = uid;
    }

    public void setShift(String shift) {
	this.shift = shift;
    }

    @Override
    public String toString() {
	return "OnCallScheduleRow [date=" + startDate + ", uid=" + uid + "]";
    }

    public OnCallScheduleRow adjustDate() {
	startDate = Dates.SORTABLE.getFormattedString(Dates.getDateDelta(Dates.SORTABLE.getDateFromString(startDate), -1));
	return this;
    }

    public boolean afterToday() {
	return startDate.compareTo(Dates.SORTABLE.getFormattedString()) > 0;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
	if (startDate == null) {
	    if (other.startDate != null)
		return false;
	} else if (!startDate.equals(other.startDate))
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
	return Dates.SORTABLE.getFormattedString().compareTo(startDate) <= 0;
    }

    public boolean before(String date) {
	return date.compareTo(this.startDate) > 0;
    }

}
