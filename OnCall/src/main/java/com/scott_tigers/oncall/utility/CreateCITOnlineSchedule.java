package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCITOnlineSchedule extends Utility implements Command {

    public static void main(String[] args) {
	new CreateCITOnlineSchedule().run();
    }

    @Override
    public void run() {
	String startDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -7);

	List<CitScheduleRow> onlineSchedule = getShiftStream()
		.filter(shift -> shift.isAfter(startDate))
		.map(CitScheduleRow::new)
		.collect(Collectors.toList());

	EngineerFiles.ONLINE_SCHEDULE.write(w -> w.json(onlineSchedule));
	launchUrl("https://oncall.corp.amazon.com/#/view/aurora-head-ams-customer-issues-team/schedule");
    }
}
