package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.test.Top100Company;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final int TOP100_POINTS = 10;
    private static final int READY_QUEUE_SIZE = 10;
    private static final String RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=06%2F01%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21#";

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

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

    private static final List<String> READY_QUEUE_COLUMNS = Arrays.asList(
	    Properties.URL,
	    Properties.CREATE_DATE,
	    Properties.WEIGHT);

    private int maxWeight;

    private void run() throws Exception {
	readPointData();
	readtop100CompanyData();
	createReadyQueue();

	successfulFileCreation(EngineerFiles.CUSTOMER_ISSUE_BACKLOG);

    }

    private void createReadyQueue() throws Exception {
	List<TT> topTickets = getTicketStreamFromUrl(RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL)
		.filter(this::notAssigned)
		.filter(this::allowableStatus)
		.peek(this::assignedWeight)
		.sorted(Comparator.comparing(TT::getWeight)
			.reversed())
		.limit(READY_QUEUE_SIZE)
		.collect(Collectors.toList());

	maxWeight = topTickets
		.stream()
		.map(TT::getWeight)
		.mapToInt(v -> v)
		.max().orElse(1000);

	topTickets.stream().forEach(this::normalizeWeight);

	EngineerFiles.CUSTOMER_ISSUE_BACKLOG
		.writeCSV(topTickets, READY_QUEUE_COLUMNS);
    }

    private boolean allowableStatus(TT tt) {
	return !unavailableStutes.contains(tt.getStatus());
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

	int weight = keywordPoints.stream().filter(keywordMatch(description))
		.map(kw -> kw.getPoints()).mapToInt(Integer::intValue)
		.sum();

	weight += Integer.valueOf(tt.getAge()) / 7;

	weight += getCompanyWeigthDelta(description, EngineerFiles.TOP_100_COMPANIES, TOP100_POINTS);
	weight += getCompanyWeigthDelta(description, EngineerFiles.ESCALATED_COMPANIES, 10);

	tt.setWeight(weight);
    }

    private long getCompanyWeigthDelta(String description, EngineerFiles companyFile, int pointsPerCompany) {
	top100Companies = companyFile
		.readCSVToPojo(Top100Company.class)
		.stream().map(Top100Company::getCompany)
		.collect(Collectors.toList());
	long weightDelta = top100Companies
		.stream()
		.filter(company -> foundIn(description, company))
		.count()
		* pointsPerCompany;
	return weightDelta;
    }

    private Predicate<? super KeywordPoints> keywordMatch(String description) {
	return kw -> foundIn(description, kw.getKeyword());
    }

    private boolean foundIn(String target, String searchString) {
	return target
		.toLowerCase()
		.contains(searchString
			.toLowerCase()
			.trim());
    }

    private void normalizeWeight(TT tt) {
	tt.setWeight(tt.getWeight() * 100 / maxWeight);

    }

}
