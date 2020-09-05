package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.List;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.newschedule.Shift;
import com.scott_tigers.oncall.shared.Dates;

public class CreateEmailListFromSchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateEmailListFromSchedule().run();
    }

    private void run() {
	String today = Dates.SORTABLE.getFormattedString();
	getShiftStream()
		.filter(s -> s.getDate().compareTo(today) >= 0)
		.map(Shift::getEngineers)
		.flatMap(List<Engineer>::stream)
		.map(Engineer::getUid)
		.distinct()
		.sorted()
		.map(uid -> uid + "@amazon.com")
		.forEach(System.out::println);
    }
}
