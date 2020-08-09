package com.scott_tigers.oncall.test;

import java.util.ArrayList;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    private ArrayList<OnCallScheduleRow> schedules = new ArrayList<OnCallScheduleRow>();
    private OnCallScheduleRow currentEvent;

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	String regex = "(BEGIN:VEVENT|DTSTART;TZID|SUMMARY:).*";
	EngineerFiles.TEST.readLines().forEach(line -> {
//	    System.out.println("line=" + (line));
	    String command = line.replaceAll(regex, "$1");
	    switch (command) {

	    case "BEGIN:VEVENT":
		newEvent();
		break;

	    case "DTSTART;TZID":
		currentEvent.setDate(line.replaceAll(".*:(\\d{4})(\\d{2})(\\d{2}).*", "$1-$2-$3"));
		break;

	    case "SUMMARY:":
		currentEvent.setUid(line.replaceAll(".* (.+)@.*", "$1"));
		break;

	    }
	});

	EngineerFiles.ON_CALL_SCHEDULE.writeCSV(schedules, OnCallScheduleRow.class);
	successfulFileCreation(EngineerFiles.ON_CALL_SCHEDULE);
    }

    private void newEvent() {
	currentEvent = new OnCallScheduleRow();
	schedules.add(currentEvent);
    }

}