package com.scott_tigers.oncall.bean;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.shared.Dates;

public class ScheduleRow {

    private String date;
    private List<Engineer> engineers;

    public ScheduleRow(String date, List<Engineer> engineers) {
	this.date = date;
	this.engineers = engineers;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public List<Engineer> getEngineers() {
	return engineers;
    }

    public void setEngineers(List<Engineer> engineers) {
	this.engineers = engineers;
    }

    public boolean scheduleComplete() {
	return Dates.SORTABLE.getFormattedString().compareTo(Dates.SORTABLE.getFormattedDelta(date, -5)) > 0;
    }

    public boolean scheduleNotComplete() {
	return !scheduleComplete();
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}
