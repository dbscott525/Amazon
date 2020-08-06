package com.scott_tigers.oncall.utility;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDailyStandupAttendeeEmails extends Utility {

    public static void main(String[] args) {
	new CreateDailyStandupAttendeeEmails().run();
    }

    private void run() {

	writeEmailsByDate(
		Stream
			.of(getCitStream(), getOncallStream())
			.flatMap(x -> x)
			.filter(OnCallScheduleRow::isCurrent)
			.collect(Collectors.toList()),
		EngineerFiles.DAILY_STAND_UP_ATTENDEE_EMAILS);
    }

    private Stream<OnCallScheduleRow> getCitStream() {
	return getScheduleRowStream()
		.map(this::getCitSingleRowStream)
		.flatMap(List<OnCallScheduleRow>::stream);
    }

    private List<OnCallScheduleRow> getCitSingleRowStream(ScheduleRow row) {
	String dateString = row.getDate();
	Date schedulDate = Dates.SORTABLE.getDateFromString(dateString);
	return Stream.of(0, 1, 2, 3, 4, 7)
		.map(day -> Dates.getDateDelta(schedulDate, day))
		.map(Dates.SORTABLE::getFormattedString)
		.map(date -> row
			.getEngineers()
			.stream()
			.map(Engineer::getUid)
			.map(uid -> new OnCallScheduleRow(date, uid)))
		.collect(Collectors.toList())
		.stream()
		.flatMap(x -> x)
		.collect(Collectors.toList());
    }

    private Stream<OnCallScheduleRow> getOncallStream() {
	return EngineerFiles.ON_CALL_SCHEDULE
		.readCSVToPojo(OnCallScheduleRow.class)
		.stream()
		.map(OnCallScheduleRow::canonicalDate);
    }

}
