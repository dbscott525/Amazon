package com.scott_tigers.oncall;

import java.util.List;

public class FindNewLevelsEngineers {

    public static void main(String[] args) {
	new FindNewLevelsEngineers().run();
    }

    private void run() {
	List<String> masterFirstNames = EngineerFiles.MASTER_LIST.getFirstNames();
	List<String> engineersFromLevels = EngineerFiles.LEVELS_FROM_QUIP.getFirstNames();
	Json.print(masterFirstNames);
	Json.print(engineersFromLevels);
	List<Engineer> t1 = Transform.list(masterFirstNames,
		x -> x.filter(y -> !engineersFromLevels.contains(y)).map(this::engFromName));
	System.out.println("t1=" + (t1));
	EngineerFiles.NEW_LEVEL_ENGINEERS.replace(t1);
    }

    private Engineer engFromName(String firstName) {
	Engineer eng = new Engineer();
	eng.setFirstName(firstName);
	// TODO Auto-generated method stub
	return eng;
    }

}
