package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class CreateRootCauseToDoList extends Utility implements TTReader {

    static String[] validRootCauseMarkers = {
	    "i.amazon.com/aurora",
	    "i.amazon.com/issues/aurora",
	    "issues.amazon.com/aurora",
	    "issues.amazon.com/issues/aurora",
	    "rds-jira.amazon.com/browse/aurora",
	    "sim.amazon.com/aurora",
	    "sim.amazon.com/aurora",
	    "sim.amazon.com/issues/aurora"
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
	rootCauseNeededTTs = getTicketStreamFromUrl(getUrl())
		.filter(this::notAssigned)
		.filter(this::noRootCause)
		.sorted(Comparator.comparing(TT::getCreateDate))
		.collect(Collectors.toList());

	getScheduleForThisWeekDeprecated().ifPresentOrElse(this::createRootCauseList,
		() -> System.out.println("No schedule is within range of today"));
    }

    private void createRootCauseList(ScheduleRow schedule) {
	engineerNames = schedule
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

    @Override
    public String getUrl() {
	String date = Dates.SORTABLE
		.convertFormat(Dates.SORTABLE
			.getFormattedDelta(EngineerFiles.ROOT_CAUSE_TO_DO
				.readCSVToPojo(TT.class)
				.stream()
				.map(TT::getCreateDate)
				.min(Comparator.comparing(String::toString))
				.orElse(getYesterdayDate())
				.substring(0, 10),
				-1),
			Dates.TT_SEARCH);

	return "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ date
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21#";
    }

    private String getYesterdayDate() {
	return Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -2);
    }

    private void assignOwner(int index) {
	rootCauseNeededTTs.get(index).setOwner(engineerNames.get(index % engineerNames.size()));
    }

    private boolean noRootCause(TT tt) {
	String rootCauseDetails = tt.getRootCauseDetails().toLowerCase();
	return Stream
		.of(validRootCauseMarkers)
		.noneMatch(rootCauseDetails::contains);
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> noRootCause(tt);
    }

}
