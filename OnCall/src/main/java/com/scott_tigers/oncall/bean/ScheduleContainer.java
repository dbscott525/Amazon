package beans;

import java.util.ArrayList;

public class ScheduleContainer {

    private ArrayList<ScheduleRow> scheduleRows;

    public ScheduleContainer(ArrayList<ScheduleRow> scheduleRows) {
	this.setScheduleRows(scheduleRows);
    }

    public ArrayList<ScheduleRow> getScheduleRows() {
	return scheduleRows;
    }

    public void setScheduleRows(ArrayList<ScheduleRow> scheduleRows) {
	this.scheduleRows = scheduleRows;
    }

}
