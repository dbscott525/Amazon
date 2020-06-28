package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.schedule.Engineer;
import com.scott_tigers.oncall.schedule.ScheduleContainer;
import com.scott_tigers.oncall.schedule.ScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class GenerateCSVSchedule {

    public static void main(String[] args) throws IOException {
	new GenerateCSVSchedule().run();
    }

    private void run() throws IOException {
	EngineerFiles.SCHEDULE_CSV
		.writeText(EngineerFiles.SCHEDULE_JSON
			.readJson(ScheduleContainer.class)
			.getScheduleRows()
			.stream()
			.map(this::toCSV)
			.collect(Collectors.joining("\n")));

	System.out.println("CSV schedule written");
    }

    private String toCSV(ScheduleRow row) {
	return Stream
		.of(Stream.of(row.getDate()),
			row
				.getEngineers()
				.stream()
				.map(Engineer::getFullName))
		.flatMap(x -> x).collect(Collectors.joining(","));
    }
}
