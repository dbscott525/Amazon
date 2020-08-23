package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.newschedule.Schedule;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ConvertScheduleToNewSchedule extends Utility {

    public static void main(String[] args) {
	new ConvertScheduleToNewSchedule().run();

    }

    private void run() {
	ScheduleContainer oldSchedule = EngineerFiles.CUSTOMER_ISSUE_TEAM_SCHEDULE.readJson(ScheduleContainer.class);
	Schedule newschedule = new Schedule(oldSchedule);
	EngineerFiles.CIT_SCHEDULE.writeJson(newschedule);
    }

}
