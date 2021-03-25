package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.ScheduleEmail;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;

public class CreateCITEmails extends Utility implements Command {

    public static void main(String[] args) {

	new CreateCITEmails().run();

    }

    @Override
    public void run() {
	String startDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -14);
	List<ScheduleEmail> list = getShiftStream()
		.map(ScheduleEmail::new)
		.filter(x -> x.getDate().compareTo(startDate) >= 0)
		.collect(Collectors.toList());

	EngineerFiles.CUSTOMER_ISSUE_EMAIL.write(w -> w.CSV(list, ScheduleEmail.class));
    }

}
