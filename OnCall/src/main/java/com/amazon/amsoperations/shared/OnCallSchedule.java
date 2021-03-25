package com.amazon.amsoperations.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.utility.Utility;

public class OnCallSchedule extends Utility {

    private static final String SUMMARY = "SUMMARY:";
    private static final String DTSTART_TZID = "DTSTART;TZID";
    private static final String DTEND_TZID = "DTEND;TZID";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";

    private EngineerType oncall;
    private List<OnlineScheduleEvent> onCallSchedules = new ArrayList<>();
    private Inputs inputs;

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
		inputs = new Inputs();
		break;

	    case DTSTART_TZID:
		inputs.setStartLine(line);
		break;

	    case DTEND_TZID:
		inputs.setEndLine(line);
		break;

	    case SUMMARY:
		processSummary(line);
		break;

	    }
	});


	return onCallSchedules;
    }
    private void processSummary(String line) {
	getUIDStream(line)
		.map(uid -> {
		    OnlineScheduleEvent currentEvent = new OnlineScheduleEvent(oncall.toString());
		    currentEvent.processStartLine(oncall, inputs.getStartLine());
		    currentEvent.processEndLine(oncall, inputs.getEndLine());
		    currentEvent.setUid(uid);
		    return currentEvent;
		})
		.forEach(event -> onCallSchedules.add(event));
    }


    private Stream<String> getUIDStream(String line) {
	return Pattern.compile(" [a-zA-Z]*?@ ")
		.matcher(line)
		.results()
		.map(MatchResult::group)
		.map(x -> x.replaceAll(" (.*?)@ ", "$1"));
    }

    private class Inputs {
	private String startLine;
	private String endLine;

	public String getStartLine() {
	    return startLine;
	}

	public void setStartLine(String startLine) {
	    this.startLine = startLine;
	}

	public String getEndLine() {
	    return endLine;
	}

	public void setEndLine(String endLine) {
	    this.endLine = endLine;
	}
    }

}
