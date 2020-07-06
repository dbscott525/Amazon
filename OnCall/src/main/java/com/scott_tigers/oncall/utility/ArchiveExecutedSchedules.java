package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ArchiveExecutedSchedules {
    public static void main(String[] args) {
	new ArchiveExecutedSchedules().run();
    }

    private void run() {
	ScheduleContainer currentSchedule = EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE
		.readJson(ScheduleContainer.class);
	ScheduleContainer executedSchedule = EngineerFiles.EXCECUTED_CUSTOMER_ISSUE_SCHEDULES
		.readJson(ScheduleContainer.class);

	List<ScheduleRow> executedSchedules = currentSchedule.getScheduleRows().stream()
		.filter(ScheduleRow::scheduleComplete).collect(Collectors.toList());
	executedSchedule.getScheduleRows().addAll(executedSchedules);
	List<ScheduleRow> currentSchedules = currentSchedule.getScheduleRows().stream()
		.filter(ScheduleRow::scheduleNotComplete).collect(Collectors.toList());
	currentSchedule.setScheduleRows(currentSchedules);

	EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE.writeJson(currentSchedule);
	EngineerFiles.EXCECUTED_CUSTOMER_ISSUE_SCHEDULES.writeJson(executedSchedule);
    }
}
