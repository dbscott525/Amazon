package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Util;

public abstract class CreateEmailsFromOnCallSchedule extends Utility {

    protected void run() {
	List<String> uids = getOnCallUIDs(getOnCallType());

	System.out.println(getOnCallType() + " Emails");
	uids
		.stream()
		.map(Util.toAmazonEmail())
		.forEach(System.out::println);

	System.out.println(getOnCallType() + " UIDs");
	uids
		.stream()
		.forEach(System.out::println);

	System.out.println(getOnCallType() + " Email String");
	String emailString = uids
		.stream()
		.map(Util.toAmazonEmail())
		.collect(Collectors.joining(";"));
	System.out.println(emailString);

    }

    protected abstract EngineerType getOnCallType();

}
