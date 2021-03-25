package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerFiles;

public class CreateAndSendOnCallEmails extends Utility {

    public static void main(String[] args) {
	new CreateAndSendOnCallEmails().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		CreateOncallCsvSchedule.class,
		CreateDailyOnCallReminderEmails.class);

	EngineerFiles.ON_CALL_DAILY_REMINDER_EMAIL.launch();
    }
}
