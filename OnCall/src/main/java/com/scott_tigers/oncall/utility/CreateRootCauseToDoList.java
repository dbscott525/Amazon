package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class CreateRootCauseToDoList extends Utility {

    static String[] validRootCauseMarkers = {
	    "https://sim.amazon.com/AURORA",
	    "https://issues.amazon.com/issues/AURORA",
	    "https://sim.amazon.com/issues/AURORA",
	    "https://i.amazon.com/issues/AURORA",
	    "https://rds-jira.amazon.com/browse/AURORA"
    };

    private static final List<String> ROOT_CAUSE_REVIEW_COLUMNS = Arrays.asList(
	    Properties.OWNER,
	    Properties.URL,
	    Properties.ROOT_CAUSE_DETAILS);

    public static void main(String[] args) throws Exception {
	new CreateRootCauseToDoList().run();
    }

    private List<String> engineerNames;
    private List<TT> rootCauseNeededTTs;

    private void run() throws Exception {
	copyMostRecentDownloadedTTs();

	rootCauseNeededTTs = EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream().filter(this::noRootCause)
		.sorted(Comparator.comparing(TT::getCreateDate))
		.collect(Collectors.toList());

	Optional<ScheduleRow> foundSchedule = EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE
		.readJson(ScheduleContainer.class)
		.getScheduleRows()
		.stream().filter(this::forToday)
		.findFirst();

	if (!foundSchedule.isPresent()) {
	    System.out.println("No schedule is within range of today");
	    return;
	}

	engineerNames = foundSchedule
		.get()
		.getEngineers()
		.stream().map(Engineer::getFirstName)
		.collect(Collectors.toList());

	engineerNames.add("Bruce");

	Collections.shuffle(engineerNames);

	IntStream.range(0, rootCauseNeededTTs.size()).forEach(this::assignOwner);

//	EngineerFiles.ROOT_CAUSE_TO_DO.writeCSV(rootCauseNeededTTs, TT.class);
	EngineerFiles.ROOT_CAUSE_TO_DO.writeCSV(rootCauseNeededTTs,
		ROOT_CAUSE_REVIEW_COLUMNS);

	successfulFileCreation(EngineerFiles.ROOT_CAUSE_TO_DO);
    }

    private void assignOwner(int index) {
	rootCauseNeededTTs.get(index).setOwner(engineerNames.get(index % engineerNames.size()));
    }

    private boolean forToday(ScheduleRow scheduleRow) {
	Date scheduleStartDate = Dates.SORTABLE_DAY_DATE.getDateFromString(scheduleRow.getDate());

	Date startDate = Dates.getDateDelta(scheduleStartDate, -2);
	Date endDate = Dates.getDateDelta(scheduleStartDate, 4);

	Date currentDate = new Date();
	return startDate.compareTo(currentDate) <= 0 && currentDate.compareTo(endDate) <= 0;
    }

    private boolean noRootCause(TT tt) {
	String rootCauseDetails = tt.getRootCauseDetails();
	return Stream
		.of(validRootCauseMarkers)
		.noneMatch(rootCauseDetails::contains);
    }

}
