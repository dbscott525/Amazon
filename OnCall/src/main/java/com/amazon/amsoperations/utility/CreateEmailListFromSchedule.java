package com.amazon.amsoperations.utility;

import java.io.IOException;
import java.util.List;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.schedule.Shift;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Util;

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
		.map(Util.toAmazonEmail())
		.forEach(System.out::println);
    }
}
