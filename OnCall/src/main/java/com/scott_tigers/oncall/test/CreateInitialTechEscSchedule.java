package com.scott_tigers.oncall.test;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.utility.Utility;

public class CreateInitialTechEscSchedule extends Utility {

    public static void main(String[] args) {
	new CreateInitialTechEscSchedule().run();
    }

    private String current;

    private void run() {
	current = "1/17/21";
	List<OnlineScheduleEvent> schedule = EngineerFiles.TECH_ESC
		.readCSVToPojo(Engineer.class)
		.stream()
		.map(eng -> {
		    OnlineScheduleEvent row = new OnlineScheduleEvent(eng.getUid(), current);
		    current = Dates.ONLINE_SCHEDULE.getFormattedDelta(current, 1);
		    return row;
		})
		.collect(Collectors.toList());
	EngineerFiles.TECH_ESC_ONLINE_SCHEDULE.write(w -> w.json(schedule));

    }
}
