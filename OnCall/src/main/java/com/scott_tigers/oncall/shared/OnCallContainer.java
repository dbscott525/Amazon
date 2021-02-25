package com.scott_tigers.oncall.shared;

import java.util.List;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;

public class OnCallContainer {
    List<OnlineScheduleEvent> schedule;

    public OnCallContainer(List<OnlineScheduleEvent> schedule) {
	this.schedule = schedule;
    }

    public List<OnlineScheduleEvent> getSchedule() {
	return schedule;
    }

}
