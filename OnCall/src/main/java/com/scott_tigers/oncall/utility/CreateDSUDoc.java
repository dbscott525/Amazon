package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDSUDoc extends Utility {

    Map<String, String> uniqueString = new HashMap<String, String>() {
	private static final long serialVersionUID = 1L;

	{
	    put("Primary0", "DeoxaEyR");
	    put("Primary1", "DBYsVrjR");
	    put("Secondary", "WSuhsnzg");
	    put("Tech Esc", "dknqZRqk");
	    put("CIT0", "eCPiAKue");
	    put("CIT1", "pFGZFkTC");
	    put("CIT2", "jBwcGwXL");
	    put("CIT3", "EvXAFSrZ");
	    put("CIT4", "EGvknsaS");
	    put("CIT5", "SrSPAEer");
	    put("CITO0", "YRZnQdLb");
	    put("CITO1", "ubXtLtsN");
	    put("CITO2", "oLDGXYPM");
	    put("CITO3", "dnKSFHnF");
	    put("CITO4", "ffyMSStF");
	    put("CITO5", "sAHGeswu");
	}
    };

    private int primaryNumber = 0;

    public static void main(String[] args) throws IOException {
	new CreateDSUDoc().run();
    }

    private String particpantDoc;

    private void run() throws IOException {
	particpantDoc = EngineerFiles.DSU_TEMPLATE.readText();

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
	    List<Engineer> engineers = getEngineeringDetails(schedule.getEngineers());
	    List<Engineer> orderedList = new ArrayList<Engineer>(engineers);
	    Collections.shuffle(engineers);
	    IntStream.range(0, engineers.size())
		    .forEach(index -> {
			makeReplacement(engineers, index, "CIT");
			makeReplacement(orderedList, index, "CITO");
		    });
	});

	makeReplacement("Primary1", "");

	makeReplacement("Today", Dates.NICE.getFormattedString());

	EngineerFiles.DSU.writeText(particpantDoc);

	successfulFileCreation(EngineerFiles.DSU);
    }

    private void makeReplacement(List<Engineer> engineers, int index, String prefix) {
	makeReplacement(prefix + index, engineers.get(index).getFullName());
    }

    private void makeReplacement(Engineer eng) {
	String type = eng.getType();
	if (type.equals("Primary")) {
	    type = type + primaryNumber;
	    primaryNumber++;
	}
	makeReplacement(type, eng.getFullName());
    }

    private void makeReplacement(String searchString, String replacement) {
	String search = Optional.ofNullable(uniqueString.get(searchString)).orElse(searchString);
	particpantDoc = particpantDoc.replace(search,
		replacement);
    }

}
