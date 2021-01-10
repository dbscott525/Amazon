package com.scott_tigers.oncall.test;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.utility.Utility;

public class CreateInitialTechEscSchedule extends Utility {

    public static void main(String[] args) {
	new CreateInitialTechEscSchedule().run();
    }

    private String current;

    private void run() {
	current = "1/17/21";
	List<Engineer> foo = EngineerFiles.TECH_ESC.readCSVToPojo(Engineer.class);
	List<CitScheduleRow> foo2 = foo.stream().map(eng -> {
	    CitScheduleRow row = new CitScheduleRow(eng.getUid(), current);
	    current = Dates.ONLINE_SCHEDULE.getFormattedDelta(current, 1);
	    return row;
	}).collect(Collectors.toList());
	Json.print(foo2);
	EngineerFiles.TECH_ESC_ONLINE_SCHEDULE.write(w -> w.json(foo2));

    }
}
