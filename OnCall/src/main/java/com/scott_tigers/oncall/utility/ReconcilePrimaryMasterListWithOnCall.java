package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerType;

public class ReconcilePrimaryMasterListWithOnCall extends Utility {

    public static void main(String[] args) {
	new ReconcilePrimaryMasterListWithOnCall().run();
    }

    private void run() {

	System.out.println("Master Primaries");
	List<String> masterPrimaries = getPrimaryStream()
		.map(Engineer::getUid)
		.peek(System.out::println)
		.collect(Collectors.toList());

	System.out.println("Oncall Primaries");
	List<String> oncallPrimaries = EngineerType.Primary
		.getHistoricalOnCallScheduleStream()
		.map(OnlineScheduleEvent::getUid)
		.distinct()
		.peek(System.out::println)
		.collect(Collectors.toList());

	biDirectionalCompare("Master", masterPrimaries, "Oncall", oncallPrimaries);

    }

    private void biDirectionalCompare(String listName1, List<String> list1, String listName2,
	    List<String> list2) {
	compareLists(listName1, list1, listName2, list2);
	compareLists(listName2, list2, listName1, list1);

    }

    private void compareLists(String listName1, List<String> list1, String listName2, List<String> list2) {
	System.out.println("Found in " + listName1 + " but not in " + listName2);
	list1.stream().filter(uid -> !list2.contains(uid)).forEach(System.out::println);

    }
}
