package com.scott_tigers.oncall.bean;

import java.util.List;

public class ScheduleContainer {

    private List<ScheduleRow> scheduleRows;

    public ScheduleContainer(List<ScheduleRow> scheduleRows) {
	this.setScheduleRows(scheduleRows);
    }

    public List<ScheduleRow> getScheduleRows() {
	return scheduleRows;
    }

    public void setScheduleRows(List<ScheduleRow> scheduleRows) {
	this.scheduleRows = scheduleRows;
    }

}
