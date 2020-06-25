package com.scott_tigers.oncall;

import java.util.List;

public class OnlineSchedule {

    private List<OnlineRow> onlineScheduleRows;

    public OnlineSchedule(List<OnlineRow> onlineScheduleRows) {
	this.setOnlineScheduleRows(onlineScheduleRows);
    }

    public List<OnlineRow> getOnlineScheduleRows() {
	return onlineScheduleRows;
    }

    public void setOnlineScheduleRows(List<OnlineRow> onlineScheduleRows) {
	this.onlineScheduleRows = onlineScheduleRows;
    }

}
