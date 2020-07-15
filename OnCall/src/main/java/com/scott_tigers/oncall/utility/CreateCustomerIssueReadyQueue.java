package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.test.Top100Company;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final String RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=06%2F01%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21#";

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

    private List<Integer> assignedTicketIds;
    private List<KeywordPoints> keywordPoints;
    private List<String> top100Companies;
    private static List<String> unavailableStutes = Arrays.asList(
	    "Pending Any Info - 7 Day Auto Resolve",
//	    "Pending Pending Customer Response",
//	    "Pending Related Item",
//	    "Code Push",
	    "Pending Requester Info - 3 Day Auto Resolve",
	    "Pending Requester Info - 7 Day Auto Resolve",
//	    "Pending Requester Information",
//	    "Pending Verification of fix",
	    "nop");

    private void run() throws Exception {
	readAssignedTickets();
	readPointData();
	readtop100CompanyData();
	createReadyQueue();

	successfulFileCreation(EngineerFiles.CUSTOMER_ISSUE_BACKLOG);

    }

    private void createReadyQueue() throws Exception {
	EngineerFiles.CUSTOMER_ISSUE_BACKLOG
		.writeCSV(getTicketStreamFromUrl(RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL)
			.filter(this::notAssigned)
			.filter(this::allowableStatus)
			.peek(this::assignedWeight)
			.sorted(Comparator.comparing(TT::getWeight)
				.reversed())
			.collect(Collectors.toList()), TT.class);
    }

    private boolean allowableStatus(TT tt) {
	return !unavailableStutes.contains(tt.getStatus());
    }

    private void readAssignedTickets() {
	assignedTicketIds = EngineerFiles.ASSIGNED_TICKETS
		.readCSVToPojo(TT.class)
		.stream()
		.map(TT::getUrl)
		.filter(url -> url.matches("https://tt.amazon.com/[0-9]+"))
		.map(url -> url.replaceAll("https://tt.amazon.com/0?([0-9]+)", "$1"))
		.map(Integer::valueOf)
		.collect(Collectors.toList());
    }

    private void readtop100CompanyData() {
	top100Companies = EngineerFiles.TOP_100_COMPANIES
		.readCSVToPojo(Top100Company.class)
		.stream().map(Top100Company::getCompany)
		.collect(Collectors.toList());
    }

    private void readPointData() {
	keywordPoints = EngineerFiles.KEYWORD_POINTS
		.readCSVToPojo(KeywordPoints.class);
    }

    private void assignedWeight(TT tt) {
	String description = tt.getDescription();

	int weight = keywordPoints.stream().filter(kw -> description.contains(kw.getKeyword()))
		.map(kw -> kw.getPoints()).mapToInt(Integer::intValue)
		.sum();

	weight += Integer.valueOf(tt.getAge()) / 7;

	if (top100Companies.stream().anyMatch(x -> description.contains(x))) {
	    weight += 5;
	}

	tt.setWeight(weight);
    }

    private boolean notAssigned(TT tt) {
	return !assignedTicketIds.contains(tt.getCaseId());
    }

}
