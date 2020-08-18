package com.scott_tigers.oncall.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateOncallSchedule extends Utility {

    private static final String SUMMARY = "SUMMARY:";
    private static final String DTSTART_TZID = "DTSTART;TZID";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";
    private ArrayList<OnCallScheduleRow> onCallSchedules = new ArrayList<OnCallScheduleRow>();
    private OnCallScheduleRow currentEvent;

    public static void main(String[] args) throws Exception {
	new CreateOncallSchedule().run();
    }

    private void run() throws Exception {

	Stream<Stream<OnCallScheduleRow>> x1 = Stream.of(getOnCallStream(), getTraineeStream());
	Stream<OnCallScheduleRow> scheduleStream = x1.flatMap(x -> x);

//	Stream<OnCallScheduleRow> scheduleStream = schedules
//		.stream();

	EngineerFiles.ON_CALL_SCHEDULE.writeCSV(
		scheduleStream
			.distinct()
			.collect(Collectors.toList()),
		OnCallScheduleRow.class);

	successfulFileCreation(EngineerFiles.ON_CALL_SCHEDULE);
    }

    private Stream<OnCallScheduleRow> getTraineeStream() {
	return EngineerFiles.TRAINING_DAILY_SCHEDULE
		.readLines()
		.stream()
		.flatMap(line -> {
		    ArrayList<OnCallScheduleRow> lines = new ArrayList<>();

		    String date = line.replaceAll("(.*?),.*", "$1");

		    Pattern p = Pattern.compile("\\(.*?\\)");
		    Matcher m = p.matcher(line);

		    while (m.find()) {
			String uid = m.group().replaceAll("\\((.*?)\\)", "$1");
			lines.add(new OnCallScheduleRow(date, uid));
		    }

		    return lines.stream();
		});
    }

    private Stream<OnCallScheduleRow> getOnCallStream() {

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

	return onCallSchedules.stream();
    }

    private void newEvent() {
	currentEvent = new OnCallScheduleRow();
	onCallSchedules.add(currentEvent);
    }

}