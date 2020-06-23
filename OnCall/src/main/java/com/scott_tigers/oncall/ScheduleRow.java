package com.scott_tigers.oncall;

public class ScheduleRow {
    public String getOnCallLead() {
	return onCallLead;
    }

    public void setOnCallLead(String onCallLead) {
	this.onCallLead = onCallLead;
    }

    public String getOnCall2() {
	return OnCall2;
    }

    public void setOnCall2(String onCall2) {
	OnCall2 = onCall2;
    }

    public String getOnCall3() {
	return OnCall3;
    }

    public void setOnCall3(String onCall3) {
	OnCall3 = onCall3;
    }

    private String onCallLead;
    private String OnCall2;
    private String OnCall3;

}
