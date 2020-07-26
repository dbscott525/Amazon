package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class FindNewLevelsEngineers extends Utility {

    private static final List<String> NEW_LEVEL_COLUMNS = Arrays.asList(
	    Properties.FIRST_NAME,
	    Properties.UID);

    public static void main(String[] args) {
	new FindNewLevelsEngineers().run();
    }

    private void run() {

	List<String> levelsUid = EngineerFiles.LEVELS_FROM_QUIP
		.readCSV()
		.stream()
		.map(Engineer::getUid)
		.collect(Collectors.toList());

	EngineerFiles.NEW_LEVEL_ENGINEERS
		.writeCSV(EngineerFiles.MASTER_LIST
			.readCSV()
			.stream()
			.filter(eng -> !levelsUid.contains(eng.getUid()))
			.collect(Collectors.toList()),
			NEW_LEVEL_COLUMNS);

	successfulFileCreation(EngineerFiles.NEW_LEVEL_ENGINEERS);
    }

}
