package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;

public class ShowOnCallEmails extends Utility {

    public static void main(String[] args) {
	new ShowOnCallEmails().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(CreateOncallSchedule.class);

	getOnCallSchedule()
		.stream()
		.map(OnCallScheduleRow::getUid)
		.distinct()
		.sorted()
		.forEach(System.out::println);

	String emails = getOnCallSchedule()
		.stream()
		.map(x -> x.getUid() + "@amazon.com")
		.distinct()
		.sorted()
		.collect(Collectors.joining(";"));

	System.out.println("emails=" + (emails));
    }
}
