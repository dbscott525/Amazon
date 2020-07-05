package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScheduleEmail {

    private String date;
    private String teamEmails;
    private String toList;
    private String teamLead;
    private String email;

    public ScheduleEmail(ScheduleRow scheduleRow, Function<List<Engineer>, List<Engineer>> transformer) {

	email = "replace@me.com";

	List<Engineer> engineers = transformer.apply(scheduleRow.getEngineers());

	teamEmails = engineers
		.stream()
		.map(Engineer::getEmail)
		.collect(Collectors.joining(";"));

	date = scheduleRow.getDate();

	toList = engineers
		.stream()
		.map(Engineer::getFirstName)
		.collect(Collectors.joining(", "))
		.replaceAll("(.+,)(.+)", "$1 and$2");

	teamLead = engineers.get(0).getFirstName();
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

    public String getTeamLead() {
	return teamLead;
    }

    public String getEmail() {
	return email;
    }

}
