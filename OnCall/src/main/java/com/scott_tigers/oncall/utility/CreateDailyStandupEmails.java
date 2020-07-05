package com.scott_tigers.oncall.utility;

import java.util.List;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyStandupEmails extends Utility {

    public static void main(String[] args) {
	new CreateDailyStandupEmails().run();
    }

    private void run() {

	List<OnCallScheduleRow> allEmails = getOnCallSchedul();
	allEmails.forEach(x -> x.setDateToDayBefore());

	writeEmailsByDate(allEmails, EngineerFiles.DAILY_STAND_UP_EMAILS);
    }

}
