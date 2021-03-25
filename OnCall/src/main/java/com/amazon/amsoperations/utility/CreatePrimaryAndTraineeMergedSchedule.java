package com.amazon.amsoperations.utility;

import java.util.List;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.EngineerType;

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