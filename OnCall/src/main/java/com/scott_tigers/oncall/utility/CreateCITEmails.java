package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.ScheduleEmail;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCITEmails extends Utility implements Command {

    public static void main(String[] args) {

	new CreateCITEmails().run();

    }

    @Override
    public void run() {
	writeCSV(
		EngineerFiles.CUSTOMER_ISSUE_EMAIL,
		ScheduleEmail.class,
		getShiftStream()
			.map(ScheduleEmail::new)
			.collect(Collectors.toList()));
    }

}
