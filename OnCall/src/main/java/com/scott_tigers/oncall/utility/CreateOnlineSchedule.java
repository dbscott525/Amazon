package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateOnlineSchedule {

    public static void main(String[] args) {
	new CreateOnlineSchedule().run();
    }

    private void run() {
	ScheduleContainer t1 = EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE.readJson(ScheduleContainer.class);
	List<CitScheduleRow> onlineSchedule = t1.getScheduleRows().stream().map(CitScheduleRow::new)
		.collect(Collectors.toList());
	EngineerFiles.ONLINE_SCHEDULE.writeJson(onlineSchedule);
    }
}
