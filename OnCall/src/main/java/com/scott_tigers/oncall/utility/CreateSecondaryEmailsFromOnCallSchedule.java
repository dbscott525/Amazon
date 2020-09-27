package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.Oncall;
import com.scott_tigers.oncall.shared.Util;

public class CreateSecondaryEmailsFromOnCallSchedule extends Utility {

    public static void main(String[] args) {
	new CreateSecondaryEmailsFromOnCallSchedule().run();
    }

    private void run() {
	Oncall.Secondary
		.getOnCallScheduleStream()
		.map(OnCallScheduleRow::getUid)
		.distinct()
		.sorted()
		.map(Util.toAmazonEmail())
		.forEach(System.out::println);

    }

}
