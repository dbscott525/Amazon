package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.schedule.Schedule;
import com.amazon.amsoperations.schedule.Shift;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;

public class RemoveFutureServerlessEngineersFromSchedule extends Utility {

    public static void main(String[] args) {
	new RemoveFutureServerlessEngineersFromSchedule().run();
    }

    private void run() {
	String today = Dates.SORTABLE.getFormattedDate();
	System.out.println("today=" + (today));
	List<Shift> shifts = getShiftStream()
		.peek(shift -> {
		    if (today.compareTo(shift.getDate()) <= 0) {
			shift.setEngineers(shift
				.getEngineers()
				.stream()
				.filter(Engineer::isNotServerless)
				.collect(Collectors.toList()));

		    }
		})
		.collect(Collectors.toList());

	EngineerFiles.CIT_SCHEDULE.write(w -> w.json(new Schedule(shifts)));

    }
}
