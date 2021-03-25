package com.amazon.amsoperations.test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.bean.Unavailability;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Json;
import com.amazon.amsoperations.utility.Utility;

public class TestUnavailbilityUpdater extends Utility {

    public static void main(String[] args) {
	new TestUnavailbilityUpdater().run();
    }

    private void run() {
	List<Engineer> engineersFromCsvFile = EngineerFiles.MASTER_LIST.readCSV();
	engineersFromCsvFile.forEach(eng -> {
	    eng.setOoo(null);
	    eng.setShiftsCompleted(0);
	});

	Map<String, Engineer> uidToEngineer = engineersFromCsvFile
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, e -> e));

	EngineerFiles.UNAVAILABILITY
		.readCSVToPojo(Unavailability.class)
		.stream()
		.forEach(ua -> ua.setOoo(uidToEngineer.get(ua.getUid())));

	getScheduleRowStreamDeprecated()
		.forEach(engineerInSchedule -> engineerInSchedule
			.getEngineers()
			.stream()
			.map(eng -> uidToEngineer.get(eng.getUid()))
			.filter(Objects::nonNull)
			.forEach(Engineer::incrementShiftsCompleted));

	Json.print(engineersFromCsvFile);
    }

}
