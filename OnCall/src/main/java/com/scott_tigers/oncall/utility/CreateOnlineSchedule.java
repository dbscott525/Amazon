package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class CreateOnlineSchedule extends Utility {

    public static void main(String[] args) {
	new CreateOnlineSchedule().run();
    }

    private void run() {
	List<CitScheduleRow> onlineSchedule = EngineerFiles
		.getScheduleRowsStream()
		.map(CitScheduleRow::new)
		.collect(Collectors.toList());
	EngineerFiles.ONLINE_SCHEDULE.writeJson(onlineSchedule);
	Json.print(onlineSchedule);
    }
}
