package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCSVSchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateCSVSchedule().run();
    }

    private void run() throws IOException {

	EngineerFiles.SCHEDULE_CSV
		.writeText(EngineerFiles
			.getScheduleRowsStream()
			.map(this::toCSV)
			.collect(Collectors.joining("\n")));

	successfulFileCreation(EngineerFiles.SCHEDULE_CSV);
    }

    private String toCSV(ScheduleRow row) {
	return Stream
		.of(Stream.of(row.getDate()),
			row
				.getEngineers()
				.stream()
				.map(this.mapToEngineerDetails())
				.map(Engineer::getFullName))
		.flatMap(x -> x).collect(Collectors.joining(","));
    }
}
