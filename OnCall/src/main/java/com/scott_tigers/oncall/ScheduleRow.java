package com.scott_tigers.oncall;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduleRow {

    private String onCallLead;
    private String OnCall2;
    private String OnCall3;
    private String date;

    public ScheduleRow(String date) {
	this.date = date;
    }

    @JsonProperty("On Call Lead")
    public String getOnCallLead() {
	return onCallLead;
    }

    public void setOnCallLead(String onCallLead) {
	this.onCallLead = onCallLead;
    }

    @JsonProperty("Primary 1")
    public String getOnCall2() {
	return OnCall2;
    }

    public void setOnCall2(String onCall2) {
	OnCall2 = onCall2;
    }

    @JsonProperty("Primary 2")
    public String getOnCall3() {
	return OnCall3;
    }

    public void setOnCall3(String onCall3) {
	OnCall3 = onCall3;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @Override
    public String toString() {
	return "ScheduleRow [date=" + date + ", onCallLead=" + onCallLead + ", OnCall2=" + OnCall2 + ", OnCall3="
		+ OnCall3 + "]";
    }

}
