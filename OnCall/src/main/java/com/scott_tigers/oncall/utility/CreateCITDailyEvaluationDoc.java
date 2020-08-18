package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCITDailyEvaluationDoc extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateCITDailyEvaluationDoc().run();
    }

    private String startDate;

    private void run() throws IOException {
	Optional<ScheduleRow> thisWeeksSchedule = getScheduleForThisWeek();
	if (thisWeeksSchedule.isPresent()) {
	    templateDoc = EngineerFiles.CIT_EVALUATION_TEMPLATE.readText();
	    startDate = Dates.ONLINE_SCHEDULE
		    .getFormattedString(Dates.SORTABLE
			    .getDateFromString(thisWeeksSchedule
				    .get()
				    .getDate()));

	    IntStream.range(0, 5).forEach(this::replaceDate);

	    replaceEngineers();

	    String fileName = EngineerFiles.CIT_EVALUATIONS.writeText(templateDoc,
		    "J:\\SupportEngineering\\CIT Evaluation\\",
		    thisWeeksSchedule.get().getDate() + " ");

	    successfulFileCreation(EngineerFiles.CIT_EVALUATIONS, fileName);
	}

    }

    private void replaceDate(int dayNum) {
	makeReplacement("Date" + dayNum, Dates.ONLINE_SCHEDULE.getFormattedDelta(startDate, dayNum));
    }

}
