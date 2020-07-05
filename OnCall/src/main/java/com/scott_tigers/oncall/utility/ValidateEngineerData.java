package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ValidateEngineerData {

    public static void main(String[] args) {
	new ValidateEngineerData().run();
    }

    private void run() {
	List<Engineer> fromOnCall = EngineerFiles.FROM_ONLINE_SCHEDULE.readCSV();
	List<Engineer> masterList = EngineerFiles.MASTER_LIST.readCSV();
	List<Engineer> masterOnCall = masterList.stream().filter(eng -> !eng.getType().equals("Trainee"))
		.collect(Collectors.toList());

	compareLists("Master On Call List", masterOnCall, "On Call List", fromOnCall);
	List<Engineer> quipLevels = EngineerFiles.LEVELS_FROM_QUIP.readCSV();
	compareLists("Master List", masterList, "Quip Levels", quipLevels);
	List<Engineer> traineesFromMasterList = masterList.stream().filter(eng -> eng.getType().equals("Trainee"))
		.collect(Collectors.toList());
	List<Engineer> citCandidatesFromPooya = EngineerFiles.CIT_CANDIDATES_FROM_POOYA.readCSV();
	compareLists("Pooya CIT Candidates", citCandidatesFromPooya, "Trainees", traineesFromMasterList);

    }

    private void compareLists(String listName1, List<Engineer> list1, String listName2, List<Engineer> list2) {
	compareListsLeftvsRight(listName1, list1, listName2, list2);
	compareListsLeftvsRight(listName2, list2, listName1, list1);
    }

    private void compareListsLeftvsRight(String leftListName, List<Engineer> leftList, String rightListName,
	    List<Engineer> rightList) {

	List<Engineer> notFound = leftList
		.stream()
		.filter(eng -> !rightList.contains(eng))
		.collect(Collectors.toList());

	System.out.println("Entries in "
		+ leftListName
		+ " not found in "
		+ rightListName
		+ ": "
		+ notFound);

    }

}
