package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.schedule.Engineer;
import com.scott_tigers.oncall.schedule.ScheduleRow;

public class ScheduleEmail {

    private String date;
    private String teamEmails;
    private String toList;

    public ScheduleEmail(ScheduleRow scheduleRow) {

	teamEmails = scheduleRow
		.getEngineers()
		.stream()
		.map(Engineer::getEmail)
		.collect(Collectors.joining(";"));

	date = scheduleRow.getDate();

	toList = scheduleRow
		.getEngineers()
		.stream()
		.map(Engineer::getFirstName)
		.collect(Collectors.joining(", "))
		.replaceAll("(.+,)(.+)", "$1 and$2");
    }

    public String getDate() {
	return date;
    }

    public String getTeamEmails() {
	return teamEmails;
    }

    public String getToList() {
	return toList;
    }

}
