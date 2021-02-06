package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OnlineScheduleEvent {

    private String startDateTime;
    private String endDateTime;
    private List<String> oncallMember;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String date;
    private String type;
    private int endHour;
    private int startHour;
    private int shiftHours;

    public OnlineScheduleEvent() {
    }

    public OnlineScheduleEvent(Shift shift) {
	oncallMember = shift
		.getEngineers()
		.stream()
		.map(Engineer::getUid)
		.collect(Collectors.toList());

	setDateTimes(shift.getDate(), -1, 8, 4, 10);
    }

    @JsonIgnore
    private void setDateTimes(String baseDate, int startDelta, int startTime, int endDelta, int endTime) {
	setDateTime(baseDate, startDelta, startTime, DateType.START);
	setDateTime(baseDate, endDelta, endTime, DateType.END);
    }

    @JsonIgnore
    private void setDateTime(String baseDate, int delta, int time, DateType dateType) {
	String computedDate = Dates.SORTABLE.getFormattedDelta(baseDate, delta);
	setDateTime(dateType, computedDate, time);
    }

    @JsonIgnore
    private void setDateTime(DateType dateType, String date, int time) {
	dateType.setDateTime(this, date, time);
    }

    public OnlineScheduleEvent(String uid, String date) {
	oncallMember = List.of(uid);
	setDateTimes(date, 0, 10, 1, 10);
    }

    public OnlineScheduleEvent(EngineerType oncall) {
    }

    public OnlineScheduleEvent(String type) {
	this.type = type;
    }

    public OnlineScheduleEvent(String startDate, int startHour, int shiftHours) {
	this.startDate = startDate;
	this.startHour = startHour;
	this.shiftHours = shiftHours;
	endHour = startHour + shiftHours;
	int dateIncrement = endHour > 24 ? 1 : 0;
	endHour = endHour > 24 ? endHour - 24 : endHour;
	setDateTimes(startDate, 0, startHour, dateIncrement, endHour);
    }

    public String getStartDateTime() {
	return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
	this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
	return endDateTime;
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

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public boolean after(String date) {
	return startDate.compareTo(date) >= 0;
    }

    public String getUid() {
	return oncallMember.get(0);
    }

    public void setUid(String uid) {
	oncallMember = List.of(uid);
    }

    public String getStartDate() {
	return startDate;
    }

    public boolean afterToday() {
	return startDate.compareTo(Dates.SORTABLE.getFormattedString()) > 0;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
	startDate = date;
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

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getEndTime() {
	return endTime;
    }

    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    public int getStartHour() {
	return startHour;
    }

    public void setStartHour(int startHour) {
	this.startHour = startHour;
    }

    public boolean before(String date) {
	return date.compareTo(this.startDate) > 0;
    }

    @JsonIgnore
    public boolean isInRange(String endDate) {
	return startDate.compareTo(endDate) < 0;
    }

    @JsonIgnore
    public OnlineScheduleEvent getNextEvent() {
	return new OnlineScheduleEvent(endDate, endHour, shiftHours);
    }

    @JsonIgnore
    public int getShiftType() {
	return Dates.SORTABLE.getDayOfWeek(startDate) * 100 + startHour;
    }

    public void processStartLine(EngineerType oncall, String line) {
	processLine(oncall, DateType.START, line);
    }

    public void processEndLine(EngineerType oncall, String line) {
	processLine(oncall, DateType.END, line);
    }

    private void processLine(EngineerType oncall, DateType type, String line) {
	String date = line.replaceAll(".*:(\\d{4})(\\d{2})(\\d{2}).*", "$1-$2-$3");
	int time = Integer.parseInt(line.replaceAll(".*?:.*?T(\\d{2})(\\d{2})\\d{2}", "$1"));
	time = oncall.getAdjustedTime(time);
	setDateTime(type, date, time);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((oncallMember == null) ? 0 : oncallMember.hashCode());
	result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
	OnlineScheduleEvent other = (OnlineScheduleEvent) obj;
	if (oncallMember == null) {
	    if (other.oncallMember != null)
		return false;
	} else if (!oncallMember.equals(other.oncallMember))
	    return false;
	if (startDate == null) {
	    if (other.startDate != null)
		return false;
	} else if (!startDate.equals(other.startDate))
	    return false;
	return true;
    }

    enum DateType {
	START {
	    @Override
	    protected void setDate(OnlineScheduleEvent onlineScheduleEvent, String date) {
		onlineScheduleEvent.startDate = date;
	    }

	    @Override
	    protected void setTime(OnlineScheduleEvent onlineScheduleEvent, int intTime, String stringTime) {
		onlineScheduleEvent.startTime = stringTime;
		onlineScheduleEvent.startHour = intTime;
	    }

	    @Override
	    protected void setDateTime(OnlineScheduleEvent onlineScheduleEvent, String dateTime) {
		onlineScheduleEvent.startDateTime = dateTime;
	    }
	},
	END {
	    @Override
	    protected void setDate(OnlineScheduleEvent onlineScheduleEvent, String date) {
		onlineScheduleEvent.endDate = date;
	    }

	    @Override
	    protected void setTime(OnlineScheduleEvent onlineScheduleEvent, int intTime, String stringTime) {
		onlineScheduleEvent.endTime = stringTime;
		onlineScheduleEvent.endHour = intTime;
	    }

	    @Override
	    protected void setDateTime(OnlineScheduleEvent onlineScheduleEvent, String dateTime) {
		onlineScheduleEvent.endDateTime = dateTime;
	    }
	};

	public void setDateTime(OnlineScheduleEvent onlineScheduleEvent, String date, int time) {

	    String stringTime = time + ":00";
	    String dateTime = Dates.SORTABLE.convertFormat(date, Dates.ONLINE_SCHEDULE) + " " + stringTime;

	    setDate(onlineScheduleEvent, date);
	    setTime(onlineScheduleEvent, time, stringTime);
	    setDateTime(onlineScheduleEvent, dateTime);
	}

	protected abstract void setDate(OnlineScheduleEvent onlineScheduleEvent, String date);

	protected abstract void setTime(OnlineScheduleEvent onlineScheduleEvent, int intTime, String stringTime);

	protected abstract void setDateTime(OnlineScheduleEvent onlineScheduleEvent, String dateTime);

    }

}
