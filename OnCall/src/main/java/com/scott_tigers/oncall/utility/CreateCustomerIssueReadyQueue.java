package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final int REQUIRED_NUMBER_OF_CUATOMER_ISSUE_TICKETS_IN_QUEUE = 10;

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

    private static final List<String> READY_QUEUE_COLUMNS = Arrays.asList(
	    Properties.ITEM,
	    Properties.URL,
	    Properties.CREATE_DATE,
	    Properties.WEIGHT,
	    Properties.DESCRIPTION);

    private List<KeywordPoints> keywordPoints;
    private int maxWeight;
    private List<TT> topTickets;

    private void run() throws Exception {
	readPointData();
	createReadyQueue();

	writeTickets(EngineerFiles.CUSTOMER_ISSUE_BACKLOG, topTickets, READY_QUEUE_COLUMNS);

    }

    private void createReadyQueue() throws Exception {

	CustomerIssueLimter limter = new CustomerIssueLimter();

	topTickets = Stream
		.of(CustomerIssueReader.class, CreateRootCauseToDoList.class)
		.map(c -> constuct(c))
		.flatMap(reader -> {
		    try {
			return getTicketStreamFromUrl(reader.getUrl())
				.filter(tt -> reader.getFilter().test(tt));
		    } catch (Exception e) {
			return Stream.<TT>empty();
		    }
		})
		.filter(this::notAssigned)
		.peek(this::fixUpForDisplay)
		.sorted(Comparator.comparing(TT::getWeight).reversed())
		.filter(tt -> limter.withinLimit(tt))
		.collect(Collectors.toList());

	maxWeight = topTickets
		.stream()
		.mapToInt(TT::getWeight)
		.max()
		.orElse(1000);

	topTickets
		.stream()
		.forEach(this::normalizeWeight);

    }

    private void readPointData() {
	keywordPoints = EngineerFiles.KEYWORD_POINTS
		.readCSVToPojo(KeywordPoints.class);
    }

    private void fixUpForDisplay(TT tt) {
	String description = tt.getDescription();

	int weight = keywordPoints.stream()
		.filter(keywordMatch(description))
		.map(kw -> kw.getPoints())
		.mapToInt(Integer::intValue)
		.sum();

	Integer intAge = Integer.valueOf(tt.getAge());
	switch (tt.getItem()) {

	case Constants.ITEM_ENGINE:
	    weight = (int) Math.pow(intAge, 2);
	    break;

	case Constants.ITEM_CUSTOMER_ISSUE:
//	    weight += intAge / 7;
	    weight += (int) Math.pow(intAge, .6);
	    break;

	}

	tt.setWeight(weight);
    }

//    private long getCompanyWeigthDelta(String description, EngineerFiles companyFile, int pointsPerCompany) {
//	long weightDelta = getCompanyList(companyFile)
//		.stream()
//		.filter(company -> foundIn(description, company))
//		.count()
//		* pointsPerCompany;
//	return weightDelta;
//    }

    private Predicate<? super KeywordPoints> keywordMatch(String description) {
	return kw -> foundIn(description, kw.getKeyword());
    }

    private void normalizeWeight(TT tt) {
	tt.setWeight(tt.getWeight() * 100 / maxWeight);
	if (tt.getItem().equals(Constants.ITEM_ENGINE)) {
	    tt.setItem("Root Cause");
	}
    }

    private class CustomerIssueLimter {
	private int customerIssues = 0;

	public boolean withinLimit(TT tt) {
	    customerIssues += tt.getItem().equals(Constants.ITEM_CUSTOMER_ISSUE) ? 1 : 0;
	    return customerIssues <= REQUIRED_NUMBER_OF_CUATOMER_ISSUE_TICKETS_IN_QUEUE;
	}

    }

}
