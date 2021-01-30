package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallMakeUp;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateOnCallMapping extends Utility {

    private static final String UPDATED_ON_CALL_MAKEUP = "Updated On Call Makeup";
    private static final String ON_CALL_MAKE_UP = "https://quip-amazon.com/WScHAveNOQlM/Organization#VFV9CAx3njr";

    public static void main(String[] args) {
	new CreateOnCallMapping().run();
    }

    private void run() {
	List<OnCallMakeUp> onCallMakeUp = readFromUrl(ON_CALL_MAKE_UP, OnCallMakeUp.class).collect(Collectors.toList());
	Map<String, Engineer> engineerMap = EngineerFiles.MASTER_LIST.readCSV().stream()
		.collect(Collectors.toMap(x -> x.getLastName() + "," + x.getFirstName(), x -> x));
	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();
	Stream<OnCallMakeUp> foo1 = onCallMakeUp.stream().map(makeup -> {
	    Optional<Engineer> foundEngineer = engineers.stream()
		    .filter(eng -> makeup.getName().toLowerCase().contains(eng.getFirstName().toLowerCase()))
		    .filter(eng -> makeup.getName().toLowerCase().contains(eng.getLastName().toLowerCase()))
		    .findFirst();
	    if (foundEngineer.isEmpty()) {
		System.out.println(makeup.getName() + " not found");
		return makeup;
	    } else {
		OnCallMakeUp newMaekup = new OnCallMakeUp();
		Engineer eng = foundEngineer.get();
		newMaekup.setName(eng.getLastName() + ", " + eng.getFirstName());
		switch (eng.getType()) {

		case "Primary":
		    newMaekup.setPrimary("X");
		    break;

		case "Secondary":
		    newMaekup.setSecondary("X");
		    break;

		}

		if (eng.isCurrent()) {
		    newMaekup.setCit("X");
		}
		return newMaekup;

	    }

//	    if (engineerMap.get(makeup.getName()) == null) {
//		System.out.println(makeup.getName() + " not found");
//
//	    }
	});
	List<OnCallMakeUp> foo2 = foo1.collect(Collectors.toList());
	EngineerFiles.UPDATED_ON_CALL_MAKEUP.write(w -> w.CSV(foo2, OnCallMakeUp.class));
    }
}
