package com.scott_tigers.oncall.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.test.Top100Company;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final int TOP100_POINTS = 10;
    private static final int READY_QUEUE_SIZE = 10;

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

    private List<KeywordPoints> keywordPoints;
    private List<String> top100Companies;
    private static final List<String> READY_QUEUE_COLUMNS = Arrays.asList(
	    Properties.ITEM,
	    Properties.URL,
	    Properties.CREATE_DATE,
	    Properties.WEIGHT,
	    Properties.DESCRIPTION);
    private int maxWeight;

    private void run() throws Exception {
	readPointData();
	readtop100CompanyData();
	createReadyQueue();

	successfulFileCreation(EngineerFiles.CUSTOMER_ISSUE_BACKLOG);

    }

    private void createReadyQueue() throws Exception {

	List<TT> topTickets = Stream
		.of(CustomerIssueReader.class, CreateRootCauseToDoList.class)
		.map(c -> constuct(c))
		.map(reader -> {
		    try {
			Predicate<TT> ttFilter = reader.getFilter();
			return getTicketStreamFromUrl(reader.getUrl()).filter(ttFilter::test);
		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		    }
		})
		.flatMap(x -> x)
		.filter(this::notAssigned)
		.peek(this::assignWeight)
		.sorted(Comparator.comparing(TT::getWeight)
			.reversed())
		.limit(READY_QUEUE_SIZE)
		.collect(Collectors.toList());

	maxWeight = topTickets
		.stream()
		.map(TT::getWeight)
		.mapToInt(v -> v)
		.max()
		.orElse(1000);

	topTickets.stream().forEach(this::normalizeWeight);

	EngineerFiles.CUSTOMER_ISSUE_BACKLOG
		.writeCSV(topTickets, READY_QUEUE_COLUMNS);
    }

    private <T> T constuct(Class<T> c) {
	try {
	    return c.getConstructor().newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	    return null;
	}
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

    private void assignWeight(TT tt) {
	String description = tt.getDescription();

	int weight = keywordPoints.stream().filter(keywordMatch(description))
		.map(kw -> kw.getPoints()).mapToInt(Integer::intValue)
		.sum();

	Integer intAge = Integer.valueOf(tt.getAge());
	switch (tt.getItem()) {

	case "Engine":
	    weight = (int) Math.pow(intAge, 1.9);
	    break;
	case "CustomerIssue":
	    weight += intAge / 7;
	    break;

	}

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
