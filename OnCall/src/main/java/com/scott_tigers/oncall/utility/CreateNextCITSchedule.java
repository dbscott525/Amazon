package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.newschedule.Schedule;
import com.scott_tigers.oncall.newschedule.ScheduleCreator;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateNextCITSchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateNextCITSchedule().run();
    }

    private void run() throws IOException {
	new ScheduleCreator()
		.startDate("2020-08-31")
//		.endDate("2020-12-31")
		.shifts(10)
		.shiftSize(6)
		.maximumShiftFrequency(4)
		.daysBetweenShifts(7)
		.create();

	writeScheduleToCSVFile();

	successfulFileCreation(EngineerFiles.SCHEDULE_CSV);

    }

    private void writeScheduleToCSVFile() {
	List<String> csvLines = EngineerFiles.CIT_SCHEDULE.readJson(Schedule.class).getShifts().stream().map(shift -> {

	    Stream<String> engineers = shift.getUids()
		    .stream().map(uid -> getEngineer(uid))
		    .sorted(Comparator.comparing(Engineer::getLevel).reversed())
		    .map(Engineer::getFullNameWithExertise);

	    return Stream.concat(Stream.of(shift.getDate()), engineers)
		    .collect(Collectors.joining(","));
	}).collect(Collectors.toList());
	EngineerFiles.SCHEDULE_CSV.writeLines(csvLines);
    }

}
