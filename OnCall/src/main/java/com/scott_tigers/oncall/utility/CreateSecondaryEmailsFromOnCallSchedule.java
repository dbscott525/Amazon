package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.Oncall;
import com.scott_tigers.oncall.shared.Util;

public class CreateSecondaryEmailsFromOnCallSchedule extends Utility {

    public static void main(String[] args) {
	new CreateSecondaryEmailsFromOnCallSchedule().run();
    }

    private void run() {
	List<String> uids = Oncall.Secondary
		.getOnCallScheduleStream()
		.map(OnCallScheduleRow::getUid)
		.distinct()
		.sorted().collect(Collectors.toList());
	System.out.println("SEONDARY EMAILS");
	uids
		.stream()
		.map(Util.toAmazonEmail())
		.forEach(System.out::println);
	System.out.println("SEONDARY UIDS");
	uids
		.stream()
		.forEach(System.out::println);

    }

}
