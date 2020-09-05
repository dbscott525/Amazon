package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyOnCallReminderEmails extends Utility implements Command {

    public static void main(String[] args) {
	new CreateDailyOnCallReminderEmails().run();
    }

    public void run() {
	writeEmailsByDate(
		getOnCallSchedule()
			.stream()
			.filter(OnCallScheduleRow::afterToday)
			.map(OnCallScheduleRow::adjustDate)
			.collect(Collectors.toList()),
		EngineerFiles.DAILY_ON_CALL_REMINDER_EMAILS);
    }

}
