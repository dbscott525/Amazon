package com.scott_tigers.oncall.test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class TestUnavailbilityUpdater {

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

	EngineerFiles.EXCECUTED_CUSTOMER_ISSUE_SCHEDULES
		.readJson(ScheduleContainer.class)
		.getScheduleRows()
		.stream()
		.forEach(engineerInSchedule -> engineerInSchedule
			.getEngineers()
			.stream()
			.map(eng -> uidToEngineer.get(eng.getUid()))
			.filter(Objects::nonNull)
			.forEach(Engineer::incrementShiftsCompleted));

	Json.print(engineersFromCsvFile);
    }

}
