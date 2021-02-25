package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyOnCallReminderEmails extends Utility implements Command {

    public static void main(String[] args) {
	new CreateDailyOnCallReminderEmails().run();
    }

    public void run() {
	List<OnlineScheduleEvent> schedule = getOnCallSchedule()
		.stream()
		.filter(OnlineScheduleEvent::afterToday)
		.collect(Collectors.toList());
	writeEmailsByDate(
		schedule,
		EngineerFiles.DAILY_ON_CALL_REMINDER_EMAILS);
    }
}
