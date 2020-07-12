package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyOnCallReminderEmails extends Utility {

    public static void main(String[] args) {
	new CreateDailyOnCallReminderEmails().run();
    }

    private void run() {

//	allEmails.map(x -> x.setDateToDayBefore());

//	writeEmailsByDate(allEmails, EngineerFiles.DAILY_ON_CALL_REMINDER_EMAILS);
	writeEmailsByDate(
		getOnCallSchedule()
			.stream()
			.filter(x -> x.afterToday())
			.map(OnCallScheduleRow::adjustDate)
			.collect(Collectors.toList()),
		EngineerFiles.DAILY_ON_CALL_REMINDER_EMAILS);
    }

}
