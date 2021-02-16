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
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.Status;

public class CreateRootCauseToDoList extends Utility implements TTReader {

    static String[] validRootCauseMarkers = {
	    "i.amazon.com/aurora",
	    "i.amazon.com/issues/aurora",
	    "issues.amazon.com/aurora",
	    "issues.amazon.com/issues/aurora",
	    "rds-jira.amazon.com/browse/aurora",
	    "sim.amazon.com/aurora",
	    "sim.amazon.com/aurora",
	    "sim.amazon.com/issues/aurora",
	    "https://t.corp.amazon.com"
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
		.filter(this::needsWork)
		.sorted(Comparator.comparing(TT::getCreateDate))
		.collect(Collectors.toList());

	getScheduleForThisWeek().ifPresentOrElse(this::createRootCauseList,
		() -> System.out.println("No schedule is within range of today"));
    }

    private void createRootCauseList(Shift shift) {
	engineerNames = shift
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
	return "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ Dates.TT_SEARCH.getFormattedDelta(Dates.TT_SEARCH.getFormattedString(),
			-Constants.ENGINE_TICKET_TRAILING_DAYS)
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21#";
    }

    private void assignOwner(int index) {
	rootCauseNeededTTs.get(index).setOwner(engineerNames.get(index % engineerNames.size()));
    }

    private boolean needsWork(TT tt) {

	if (tt.getDescription().contains("FoundIncorrectUserPermissions")) {
	    return false;
	}

	if (tt.getAge().equals("0")) {
	    return false;
	}

	Predicate<TT> needsWork = t -> Status.get(t.getStatus()).needsWork();
	Predicate<TT> noRootCause = t -> {
	    String rootCauseDetails = t.getRootCauseDetails().toLowerCase();
	    return Stream
		    .of(validRootCauseMarkers)
		    .noneMatch(rootCauseDetails::contains);
	};

	return Stream.of(needsWork, noRootCause).anyMatch(p -> p.test(tt));
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> needsWork(tt);
//	return tt -> unresolved(tt) && !tt.getDescription().contains("Cross Issue Event - CIE");
    }

    @Override
    public String getTitle() {
	return "Engine";

    }

    @Override
    public void printReport() {
    }

}
