package com.scott_tigers.oncall.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateOncallSchedule extends Utility {

    private static final String SUMMARY = "SUMMARY:";
    private static final String DTSTART_TZID = "DTSTART;TZID";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";
    private ArrayList<OnCallScheduleRow> schedules = new ArrayList<OnCallScheduleRow>();
    private OnCallScheduleRow currentEvent;

    public static void main(String[] args) throws Exception {
	new CreateOncallSchedule().run();
    }

    private void run() throws Exception {

	Stream
		.of("https://oncall.corp.amazon.com/#/view/aurora-head-primary/schedule",
			"https://oncall.corp.amazon.com/#/view/aurora-head-secondary/schedule",
			"https://oncall.corp.amazon.com/#/view/aurora-tech-escalation/schedule")
		.map(this::launchUrlAndWaitForDownload)
		.map(EngineerFiles::readLines)
		.flatMap(List<String>::stream)
		.forEach(line -> {

		    switch (line.replaceAll("(" + BEGIN_VEVENT + "|" + DTSTART_TZID + "|" + SUMMARY + ").*", "$1")) {

		    case BEGIN_VEVENT:
			newEvent();
			break;

		    case DTSTART_TZID:
			currentEvent.setDate(line.replaceAll(".*:(\\d{4})(\\d{2})(\\d{2}).*", "$1-$2-$3"));
			break;

		    case SUMMARY:
			currentEvent.setUid(line.replaceAll(".* (.+)@.*", "$1"));
			break;

		    }
		});

	EngineerFiles.ON_CALL_SCHEDULE.writeCSV(
		schedules
			.stream()
			.distinct()
			.collect(Collectors.toList()),
		OnCallScheduleRow.class);

	successfulFileCreation(EngineerFiles.ON_CALL_SCHEDULE);
    }

    private void newEvent() {
	currentEvent = new OnCallScheduleRow();
	schedules.add(currentEvent);
    }

}