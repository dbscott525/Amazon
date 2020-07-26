package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class TruncateCustomerIssueTeamSchedule extends Utility {

    private static final String LAST_DATE = "2020-07-31";

    public static void main(String[] args) {
	new TruncateCustomerIssueTeamSchedule().run();
    }

    private void run() {
	EngineerFiles.writeScheduleRows(EngineerFiles
		.getScheduleRowsStream()
		.filter(row -> row.isBefore(LAST_DATE))
		.collect(Collectors.toList()));
    }

}
