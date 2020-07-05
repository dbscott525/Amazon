package com.scott_tigers.oncall.utility;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyOnCallEmails extends Utility {

    public static void main(String[] args) {
	new CreateDailyOnCallEmails().run();
    }

    private void run() {

	List<OnCallScheduleRow> allEmails = getOnCallSchedul();

	List<OnCallScheduleRow> citEmails = EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE
		.readJson(ScheduleContainer.class)
		.getScheduleRows()
		.stream().map(row -> {
		    String dateString = row.getDate();
		    Date schedulDate = Dates.SORTABLE.getDateFromString(dateString);
		    return Stream.of(0, 1, 2, 3, 4, 7)
			    .map(day -> Dates.getDateDelta(schedulDate, day))
			    .map(Dates.SORTABLE::getFormattedString)
			    .map(date -> {
				return row.getEngineers()
					.stream()
					.map(Engineer::getUid)
					.map(uid -> new OnCallScheduleRow(date, uid));
			    })
			    .collect(Collectors.toList())
			    .stream()
			    .flatMap(x -> x)
			    .collect(Collectors.toList());
		})
		.flatMap(List<OnCallScheduleRow>::stream)
		.collect(Collectors.toList());

	allEmails.addAll(citEmails);

	writeEmailsByDate(allEmails, EngineerFiles.DAILY_STAND_UP_EMAILS);
    }

}
