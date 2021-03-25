package com.scott_tigers.oncall.utility;

import java.util.List;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreatePrimaryAndTraineeMergedSchedule extends Utility {

    private static final boolean USE_SAVED_SCHEDULE = false;

    public static void main(String[] args) {
	new CreatePrimaryAndTraineeMergedSchedule().run();
    }

    private void run() {
	List<OnlineScheduleEvent> primarySchedule = readOnCallSchedule(EngineerType.Primary, USE_SAVED_SCHEDULE)
		.getSchedule();

	mergeOnCallTrainees(primarySchedule);

	EngineerFiles.PRIMARY_ONCALL_SCHEDULE.write(w -> w.json(primarySchedule));

    }

}