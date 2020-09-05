package com.scott_tigers.oncall.utility;

import java.io.IOException;

import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateAvailabilitySchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateAvailabilitySchedule().run();
    }

    private void run() throws IOException {

	createFileFromTemplate(EngineerFiles.AVAILABILTY_SCHEDULE_TEMPLATE, EngineerFiles.AVAILABILTY_SCHEDULE,
		() -> replaceEngineers(getShiftStream()
			.filter(x -> x.getDate().compareTo(Dates.SORTABLE.getNextMondayFormattedDate()) == 0).findFirst()));

    }

}
