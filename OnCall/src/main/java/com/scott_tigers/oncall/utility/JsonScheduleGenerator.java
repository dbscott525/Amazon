package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.EngineerDetail;
import com.scott_tigers.oncall.schedule.OnCallSchedule;
import com.scott_tigers.oncall.schedule.OnlineRow;
import com.scott_tigers.oncall.shared.CSVReader;
import com.scott_tigers.oncall.shared.Constants;

public class JsonScheduleGenerator {

    public static void main(String[] args) {
	new JsonScheduleGenerator().run();
    }

    private Map<String, String> nameToUid;

    private void run() {
	nameToUid = new CSVReader<EngineerDetail>()
		.inputFile(Constants.ENGINEER_DETAIL_FILE)
		.type(EngineerDetail.class)
		.read()
		.stream()
		.collect(Collectors.toMap(EngineerDetail::getFirstName, EngineerDetail::getUid));

	String jsonRows = new GsonBuilder()
		.setPrettyPrinting()
		.create()
		.toJson(new CSVReader<OnCallSchedule>()
			.inputFile(Constants.EXISTING_PUBLISHED_SCHEDULE)
			.type(OnCallSchedule.class)
			.read()
			.stream()
			.sorted(Comparator.comparing(OnCallSchedule::getDate))
			.map(this::getDaySchedule)
			.collect(Collectors.toList()));

	try {
	    Files.write(Paths.get(Constants.SCHEDULE_JSON_FILE), jsonRows.getBytes());
	} catch (IOException e) {
	    e.printStackTrace();
	}

	System.out.println(jsonRows);
    }

    private OnlineRow getDaySchedule(OnCallSchedule onCallSchedule) {
	return new OnlineRow(onCallSchedule, nameToUid);
    }

}
