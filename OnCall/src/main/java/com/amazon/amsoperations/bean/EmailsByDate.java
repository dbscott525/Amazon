package com.amazon.amsoperations.bean;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.amazon.amsoperations.shared.Constants;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Util;

public class EmailsByDate {

    private String date;
    private String emails;
    private String to;
    private String email = Constants.REPLACE_ME_EMAIL;

    public static EmailsByDate fromTrainee(Entry<String, List<Engineer>> entry) {
	return new EmailsByDate().trainee(entry);
    }

    public EmailsByDate(Entry<String, List<OnlineScheduleEvent>> entry) {
	date = Dates.SORTABLE.getFormattedDelta(entry.getKey(), -1);

	emails = entry
		.getValue()
		.stream()
		.map(this::uidToEmail)
		.collect(Collectors.joining(";"));
    }

    private String getSortableDate(String date) {
	return Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE);
    }

    public EmailsByDate trainee(Entry<String, List<Engineer>> entry) {
	date = getSortableDate(entry.getKey());
	List<Engineer> engineers = entry.getValue();
	emails = Util.getEngineerEmails(engineers);
	to = Util.getEngineerToList(engineers);
	return this;
    }

    public EmailsByDate() {
    }

    private String uidToEmail(OnlineScheduleEvent onCallScheduleEvent) {
	return onCallScheduleEvent.getUid() + Constants.AMAZON_EMAIL_POSTFIX;
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
