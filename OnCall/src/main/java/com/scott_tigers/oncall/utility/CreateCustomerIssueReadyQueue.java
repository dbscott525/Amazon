package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.TicketStatuses;
import com.scott_tigers.oncall.shared.Util;
import com.scott_tigers.oncall.test.Top100Company;

public class CreateCustomerIssueReadyQueue {

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

    private List<Integer> assignedTicketIds;
    private List<KeywordPoints> keywordPoints;
    private List<String> top100Companies;
    private static List<String> unavailableStutes = Arrays.asList(
	    TicketStatuses.PENDING_REQUESTER_INFO_7_DAY_AUTO_RESOLVE,
	    TicketStatuses.PENDING_PENDING_CUSTOMER_RESPONSE,
	    TicketStatuses.PENDING_ANY_INFO_7_DAY_AUTO_RESOLVE);

    private void run() throws Exception {
	readAssignedTickets();
	Util.makeCopyofMostRecentTTDownload();
	readPointData();
	readtop100CompanyData();
	createReadyQueue();

	System.out.println("TT Ready Queue Created at "
		+ EngineerFiles.CUSTOMER_ISSUE_BACKLOG.getFileName());

    }

    private void createReadyQueue() {
	EngineerFiles.CUSTOMER_ISSUE_BACKLOG.writeCSV(EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream()
		.filter(this::notAssigned)
		.filter(this::allowableStatus)
		.peek(tt -> assignedWeight(tt))
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
