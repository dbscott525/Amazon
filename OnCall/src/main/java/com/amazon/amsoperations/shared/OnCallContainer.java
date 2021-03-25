package com.amazon.amsoperations.shared;

import java.util.ArrayList;
import java.util.List;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;

public class OnCallContainer {
    List<OnlineScheduleEvent> schedule;

    public OnCallContainer(List<OnlineScheduleEvent> schedule) {
	this.schedule = schedule;
    }

    public OnCallContainer() {
	schedule = new ArrayList<>();
    }

    public List<OnlineScheduleEvent> getSchedule() {
	return schedule;
    }

}
