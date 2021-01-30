package com.scott_tigers.oncall.shared;

import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;

public enum Oncall {

    Primary("https://oncall.corp.amazon.com/#/view/aurora-head-primary/schedule"),
    Secondary("https://oncall.corp.amazon.com/#/view/aurora-head-secondary/schedule"),
    TechEsc("https://oncall.corp.amazon.com/#/view/aurora-tech-escalation/schedule") {
	@Override
	public boolean isEngineer() {
	    return false;
	}
    };

    private String url;

    Oncall(String url) {
	this.url = url;
    }

    public OnCallSchedule getOnCallSchedule() {
	return new OnCallSchedule(this);
    }

    String getUrl() {
	return url;
    }

    public Stream<OnCallScheduleRow> getOnCallScheduleStream() {
	return getOnCallSchedule()
		.getOnCallScheduleList()
		.stream();
    }

    public boolean isEngineer() {
	return true;
    }

}
