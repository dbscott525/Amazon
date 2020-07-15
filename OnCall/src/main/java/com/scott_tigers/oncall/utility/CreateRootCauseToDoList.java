package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
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
	    "https://i.amazon.com/AURORA",
	    "https://rds-jira.amazon.com/browse/AURORA",
	    "https://issues.amazon.com/AURORA"
    };

    private static final List<String> ROOT_CAUSE_REVIEW_COLUMNS = Arrays.asList(
	    Properties.OWNER,
	    Properties.CREATE_DATE,
	    Properties.URL,
	    Properties.ROOT_CAUSE_DETAILS);

    public static void main(String[] args) throws Exception {
	new CreateRootCauseToDoList().run();
    }

    private List<String> engineerNames;
    private List<TT> rootCauseNeededTTs;

    private void run() throws Exception {
//	copyMostRecentDownloadedTTs();
	rootCauseNeededTTs = getTicketStreamFromUrl(getUrl())
		.filter(this::noRootCause)
		.sorted(Comparator.comparing(TT::getCreateDate))
		.collect(Collectors.toList());

	Optional<ScheduleRow> foundSchedule = getScheduleForThisWeek();

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

	EngineerFiles.ROOT_CAUSE_TO_DO.writeCSV(rootCauseNeededTTs,
		ROOT_CAUSE_REVIEW_COLUMNS);

	successfulFileCreation(EngineerFiles.ROOT_CAUSE_TO_DO);
    }

    private String getUrl() {
	String url = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ Dates.SORTABLE
			.convertFormat(Dates.SORTABLE
				.getFormattedDelta(EngineerFiles.ROOT_CAUSE_TO_DO
					.readCSVToPojo(TT.class)
					.stream()
					.map(TT::getCreateDate)
					.min(Comparator.comparing(String::toString))
					.orElse("000")
					.substring(0, 10),
					-1),
				Dates.TT_SEARCH)
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21";
	return url;
    }

    private void assignOwner(int index) {
	rootCauseNeededTTs.get(index).setOwner(engineerNames.get(index % engineerNames.size()));
    }

    private boolean noRootCause(TT tt) {
	String rootCauseDetails = tt.getRootCauseDetails();
	return Stream
		.of(validRootCauseMarkers)
		.noneMatch(rootCauseDetails::contains);
    }

}
