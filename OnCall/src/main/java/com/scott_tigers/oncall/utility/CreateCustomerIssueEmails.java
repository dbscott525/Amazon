package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.ScheduleEmail;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCustomerIssueEmails extends Utility {

    public static void main(String[] args) {

	new CreateCustomerIssueEmails().run();

    }

    private void run() {

	List<ScheduleEmail> scheduleEmails = EngineerFiles
		.getScheduleRowsStream()
		.map(scheduleRow -> new ScheduleEmail(scheduleRow, getEngineerListTransformer()))
		.collect(Collectors.toList());

	EngineerFiles.CUSTOMER_ISSUE_EMAIL
		.writeCSV(scheduleEmails,
			ScheduleEmail.class);

	successfulFileCreation(EngineerFiles.CUSTOMER_ISSUE_EMAIL);
    }

}
