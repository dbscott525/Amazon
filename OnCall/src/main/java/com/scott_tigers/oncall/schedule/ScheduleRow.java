package com.scott_tigers.oncall.schedule;

import java.util.List;

import com.google.gson.GsonBuilder;

public class ScheduleRow {

    private String date;
    private List<Engineer> engineers;

    public ScheduleRow(String date, List<Engineer> engineers) {
	this.date = date;
	this.setEngineers(engineers);
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

//    @Override
//    public String toString() {
//	return "ScheduleRow [date=" + date + ", engineers=" + engineers + "]";
//    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}
