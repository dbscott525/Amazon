package com.scott_tigers.oncall.shared;

import java.util.ArrayList;
import java.util.List;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.utility.Utility;

public class OnCallSchedule extends Utility {

    private static final String SUMMARY = "SUMMARY:";
    private static final String DTSTART_TZID = "DTSTART;TZID";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";

    private Oncall oncall;
    private OnCallScheduleRow currentEvent;
    private List<OnCallScheduleRow> onCallSchedules = new ArrayList<>();

    public OnCallSchedule(Oncall oncall) {
	this.oncall = oncall;
    }

    public List<OnCallScheduleRow> getOnCallScheduleList() {
	String fileName = launchUrlAndWaitForDownload(oncall.getUrl());
	List<String> lines = EngineerFiles.readLines(fileName);
	lines.forEach(line -> {

	    switch (line.replaceAll("(" + BEGIN_VEVENT + "|" + DTSTART_TZID + "|" + SUMMARY + ").*",
		    "$1")) {

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

	return onCallSchedules;
    }

    private void newEvent() {
	currentEvent = new OnCallScheduleRow(oncall.toString());
	onCallSchedules.add(currentEvent);
    }

}
