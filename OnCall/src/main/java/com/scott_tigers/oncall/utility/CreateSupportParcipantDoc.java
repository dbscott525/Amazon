package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateSupportParcipantDoc extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateSupportParcipantDoc().run();
    }

    private String particpantDoc;

    private void run() throws IOException {
	particpantDoc = EngineerFiles.SUPPORT_PARTICPANTS_TEMPLATE.readText();

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

	getScheduleForThisWeek().ifPresent(schedule -> {
	    List<Engineer> engineers = schedule.getEngineers();
	    Collections.shuffle(engineers);
	    IntStream.range(0, engineers.size())
		    .forEach(index -> makeReplacement("CIT" + (index + 1), engineers.get(index).getFullName()));
	});

	makeReplacement("Today", Dates.NICE.getFormattedString());

	EngineerFiles.SUPPORT_PARTICPANTS.writeText(particpantDoc);

	successfulFileCreation(EngineerFiles.SUPPORT_PARTICPANTS);
    }

    private void makeReplacement(Engineer eng) {
	makeReplacement(eng.getType(), eng.getFullName());
    }

    private void makeReplacement(String type, String name) {
	particpantDoc = particpantDoc.replace("[" + type + "]", name);
    }

}
