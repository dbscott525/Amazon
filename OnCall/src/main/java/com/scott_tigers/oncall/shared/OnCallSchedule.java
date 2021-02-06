package com.scott_tigers.oncall.shared;

import java.util.ArrayList;
import java.util.List;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.utility.Utility;

public class OnCallSchedule extends Utility {

    private static final String SUMMARY = "SUMMARY:";
    private static final String DTSTART_TZID = "DTSTART;TZID";
    private static final String DTEND_TZID = "DTEND;TZID";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";

    private EngineerType oncall;
    private OnlineScheduleEvent currentEvent;
    private List<OnlineScheduleEvent> onCallSchedules = new ArrayList<>();

    public OnCallSchedule(EngineerType oncall) {
	this.oncall = oncall;
    }

    public List<OnlineScheduleEvent> getOnCallScheduleList(String url) {
	String fileName = launchUrlAndWaitForDownload(url);
	List<String> lines = EngineerFiles.readLines(fileName);
	lines.forEach(line -> {

	    switch (line.replaceAll("(" + BEGIN_VEVENT + "|" + DTSTART_TZID + "|" + DTEND_TZID + "|" + SUMMARY + ").*",
		    "$1")) {

	    case BEGIN_VEVENT:
		newEvent();
		break;

	    case DTSTART_TZID:
		currentEvent.processStartLine(oncall, line);
		break;

	    case DTEND_TZID:
		currentEvent.processEndLine(oncall, line);
		break;

	    case SUMMARY:
		currentEvent.setUid(line.replaceAll(".* (.+)@.*", "$1"));
		break;

	    }
	});

	return onCallSchedules;
    }

    private void newEvent() {
	currentEvent = new OnlineScheduleEvent(oncall.toString());
	onCallSchedules.add(currentEvent);
    }

}
