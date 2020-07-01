package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.ScheduleEmail;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Transform;

public class GenerateCustomerIssueEmails {

    public static void main(String[] args) {
	EngineerFiles.CUSTOMER_ISSUE_EMAIL
		.writeCSV(Transform
			.list(EngineerFiles.CURRENT_SCHEDULE.readJson(ScheduleContainer.class)
				.getScheduleRows(), x -> x.map(ScheduleEmail::new)),
			ScheduleEmail.class);

	System.out.println("Customer email list create at "
		+ EngineerFiles.CUSTOMER_ISSUE_EMAIL.getFileName());

    }

}
