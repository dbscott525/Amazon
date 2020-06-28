package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.schedule.ScheduleContainer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Transform;

public class GenerateCustomerIssueEmails {

    public static void main(String[] args) {
	EngineerFiles.CUSTOMER_ISSUE_EMAIL
		.writeCSV(Transform
			.list(EngineerFiles.SCHEDULE_JSON.readJson(ScheduleContainer.class)
				.getScheduleRows(), x -> x.map(ScheduleEmail::new)),
			ScheduleEmail.class);

    }

}
