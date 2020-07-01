package com.scott_tigers.oncall.bean;

import java.util.stream.Collectors;

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
