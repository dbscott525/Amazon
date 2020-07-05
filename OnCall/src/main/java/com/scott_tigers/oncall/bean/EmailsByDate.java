package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.Constants;

public class EmailsByDate {

    private String date;
    private String emails;
    private String email = "replace@me.com";

    public EmailsByDate(Entry<String, List<OnCallScheduleRow>> entry) {
	date = entry.getKey();
	emails = entry
		.getValue()
		.stream()
		.map(this::uidToEmail)
		.collect(Collectors.joining(";"));
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

}
