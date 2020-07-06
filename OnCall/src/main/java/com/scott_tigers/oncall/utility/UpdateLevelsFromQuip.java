package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class UpdateLevelsFromQuip {

    public static void main(String[] args) {
	new UpdateLevelsFromQuip().run();
    }

    private Map<String, Engineer> levelEngineers;

    private void run() {
	levelEngineers = EngineerFiles.LEVELS_FROM_QUIP
		.readCSV()
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, Function.identity()));
	List<Engineer> masterList = EngineerFiles.MASTER_LIST.readCSV();
	masterList.stream().forEach(this::updateLevel);
	EngineerFiles.MASTER_LIST.replace(masterList);
//	EngineerFiles.TEST.replace(masterList);

	System.out.println("Levels updated");
    }

    private void updateLevel(Engineer eng) {
	eng.setLevel(levelEngineers.get(eng.getUid()).getLevel());
    }
}
