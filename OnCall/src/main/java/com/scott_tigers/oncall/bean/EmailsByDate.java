package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Util;

public class EmailsByDate {

    private String date;
    private String emails;
    private String to;
    private String email = "replace@me.com";

    public static EmailsByDate fromTrainee(Entry<String, List<Engineer>> entry) {
	return new EmailsByDate().trainee(entry);
    }

    public EmailsByDate(Entry<String, List<OnCallScheduleRow>> entry) {
	date = entry.getKey();

	emails = entry
		.getValue()
		.stream()
		.map(this::uidToEmail)
		.collect(Collectors.joining(";"));
    }

    public EmailsByDate trainee(Entry<String, List<Engineer>> entry) {
	date = entry.getKey();
	List<Engineer> engineers = entry.getValue();
	emails = Util.getEngineerEmails(engineers);
	to = Util.getEngineerToList(engineers);
	return this;
    }

    public EmailsByDate() {
    }

    private String uidToEmail(OnCallScheduleRow onCallScheduleRow) {
	return onCallScheduleRow.getUid() + Constants.AMAZON_EMAIL_POSTFIX;
    }

    public String getDate() {
	return date;
    }

    public String getEmails() {
	return emails;
    }

    public String getEmail() {
	return email;
    }

    public String getTo() {
	return to;
    }

}
