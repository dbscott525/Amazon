package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class CreateCITOnlineSchedule extends Utility {

    public static void main(String[] args) {
	new CreateCITOnlineSchedule().run();
    }

    private void run() {
	String startDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -7);

	List<CitScheduleRow> onlineSchedule = getShiftStream()
		.filter(shift -> shift.isAfter(startDate))
		.map(CitScheduleRow::new)
		.collect(Collectors.toList());
	EngineerFiles.ONLINE_SCHEDULE.writeJson(onlineSchedule);
	Json.print(onlineSchedule);
    }
}
