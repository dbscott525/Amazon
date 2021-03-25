package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;

public class CreateCITOnlineSchedule extends Utility implements Command {

    public static void main(String[] args) {
	new CreateCITOnlineSchedule().run();
    }

    @Override
    public void run() {
	String startDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -7);

	List<OnlineScheduleEvent> onlineSchedule = getShiftStream()
		.filter(shift -> shift.isAfter(startDate))
		.map(OnlineScheduleEvent::new)
		.collect(Collectors.toList());

	EngineerFiles.ONLINE_SCHEDULE.write(w -> w.json(onlineSchedule));
	launchUrl("https://oncall.corp.amazon.com/#/view/aurora-head-ams-customer-issues-team/schedule");
    }
}
