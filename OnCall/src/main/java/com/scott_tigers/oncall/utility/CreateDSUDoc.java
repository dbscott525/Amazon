package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDSUDoc extends Utility {

    private int primaryNumber = 0;

    public static void main(String[] args) throws IOException {
	new CreateDSUDoc().run();
    }

    private void run() throws IOException {
	templateDoc = EngineerFiles.DSU_TEMPLATE.readText();

	Map<String, String> techEscMap = EngineerFiles.TECH_ESC
		.readCSVToPojo(Engineer.class)
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, Engineer::getFullName));

	String today = Dates.SORTABLE.getFormattedString();

	getOnCallSchedule()
		.stream()
		.filter(eng -> eng.getDate().equals(today))
		.forEach(onCall -> {

		    Optional
			    .ofNullable(getEngineer(onCall.getUid()))
			    .ifPresent(this::makeReplacement);

		    Optional.ofNullable(techEscMap.get(onCall.getUid()))
			    .ifPresent(techEscName -> makeReplacement("Tech Esc", techEscName));
		});

	replaceEngineers();

	makeReplacement("Primary1", "");

	makeReplacement("Today", Dates.NICE.getFormattedString());

	EngineerFiles.DSU.writeText(templateDoc);

	successfulFileCreation(EngineerFiles.DSU);
    }

    private void makeReplacement(Engineer eng) {
	String type = eng.getType();
	if (type.equals("Primary")) {
	    type = type + primaryNumber;
	    primaryNumber++;
	}
	makeReplacement(type, eng.getFullName());
    }

}
