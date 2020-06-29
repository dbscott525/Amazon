package com.scott_tigers.oncall.utility;

import java.util.List;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.Transform;

import beans.Engineer;

public class FindNewLevelsEngineers {

    public static void main(String[] args) {
	new FindNewLevelsEngineers().run();
    }

    private void run() {
	List<String> masterFirstNames = EngineerFiles.MASTER_LIST.getFirstNames();
	List<String> engineersFromLevels = EngineerFiles.LEVELS_FROM_QUIP.getFirstNames();
	Json.print(masterFirstNames);
	Json.print(engineersFromLevels);
	EngineerFiles.NEW_LEVEL_ENGINEERS.replace(Transform.list(masterFirstNames,
		x -> x.filter(y -> !engineersFromLevels.contains(y)).map(this::engFromName)));
    }

    private Engineer engFromName(String firstName) {
	Engineer eng = new Engineer();
	eng.setFirstName(firstName);
	return eng;
    }

}
