package com.scott_tigers.oncall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.Gson;

public class OnlineRow {

    private String startDateTime;
    private String endDateTime;
    private List<String> oncallMember;

    public OnlineRow(OnCallSchedule onCallSchedule, Map<String, String> nameToUid) {
	startDateTime = getDate(onCallSchedule.getDate(), 0);
	endDateTime = getDate(onCallSchedule.getDate(), 1);

	oncallMember = new ArrayList<Supplier<String>>() {
	    private static final long serialVersionUID = 1L;

	    {
		add(() -> onCallSchedule.getOnCallLead());
		add(() -> onCallSchedule.getOnCall2());
		add(() -> onCallSchedule.getOnCall3());
	    }
	}
		.stream()
		.map(Supplier<String>::get)
		.map(nameToUid::get)
		.collect(Collectors.toList());
    }

    public OnlineRow() {
	// TODO Auto-generated constructor stub
    }

    public String getStartDateTime() {
	return startDateTime;
    }

    public String getEndDateTime() {
	return endDateTime;
    }

    @Override
    public String toString() {
	return new Gson().toJson(this);
    }

    public void setStartDateTime(String startDateTime) {
	this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
	this.endDateTime = endDateTime;
    }

    public List<String> getOncallMember() {
	return oncallMember;
    }

    public void setOncallMember(List<String> oncallMember) {
	this.oncallMember = oncallMember;
    }

    String getDate(Date date, int dayIncrement) {
	SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, dayIncrement + 1);
	String dateString = sdf.format(c.getTime()) + " 10:00";
	return dateString;
    }
}