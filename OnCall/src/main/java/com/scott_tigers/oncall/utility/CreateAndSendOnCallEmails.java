package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateAndSendOnCallEmails extends Utility {

    public static void main(String[] args) {
	new CreateAndSendOnCallEmails().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		CreateOncallSchedule.class,
		CreateDailyOnCallReminderEmails.class);

	EngineerFiles.ON_CALL_DAILY_REMINDER_EMAIL.launch();
    }
}
