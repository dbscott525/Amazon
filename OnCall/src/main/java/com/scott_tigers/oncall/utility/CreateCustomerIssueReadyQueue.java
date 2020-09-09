package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final int NORMALIZED_MAXIMUM_WEIGHT = 100;
    private static final double CUSTOMER_ISSUE_AGE_EXPONENT = .6;
    private static final int MAXIMUM_TICKETS_PER_TYPE = 7;
    private static final String[] READY_QUEUE_COLUMNS = {
	    Properties.ITEM,
	    Properties.URL,
	    Properties.CREATE_DATE,
	    Properties.WEIGHT,
	    Properties.DESCRIPTION };

    private List<KeywordPoints> keywordPoints;
    private int maxCustomerIssueWeight;
    private List<TT> topTickets;

    private double engineExp;

    public static void main(String[] args) throws Exception {
	new CreateCustomerIssueReadyQueue().run();
    }

    private void run() throws Exception {
	readPointData();
	createReadyQueue();

	EngineerFiles.CUSTOMER_ISSUE_BACKLOG.write(w -> w.CSV(topTickets, READY_QUEUE_COLUMNS));

    }

    private void createReadyQueue() throws Exception {

	CustomerIssueLimter limter = new CustomerIssueLimter();
	List<TT> ciTickets = getTickets(new CustomerIssueReader());

	maxCustomerIssueWeight = getMaximum(ciTickets, TT::getWeight, 1000);

	System.out.println("maxWeight=" + (maxCustomerIssueWeight));
	List<TT> engineTiockets = getTickets(new CreateRootCauseToDoList());
	int maxEnginerAge = getMaximum(engineTiockets, TT::getIntAge, 0);

	engineExp = Math.log(maxCustomerIssueWeight * .6) / Math.log(maxEnginerAge);
	System.out.println("engineExp=" + (engineExp));
	System.out.println("maxAge=" + (maxEnginerAge));

	Stream<TT> engineStream = engineTiockets.stream().peek(this::fixUpForDisplay);
	Stream<TT> ciStream = ciTickets.stream();

	topTickets = Stream.concat(engineStream, ciStream)
		.sorted(Comparator.comparing(TT::getWeight).reversed())
		.filter(tt -> limter.withinLimit(tt))
//		.peek(this::normalizeWeight)
		.collect(Collectors.toList());

	topTickets
		.stream()
		.forEach(this::normalizeWeight);

    }

    private int getMaximum(List<TT> ticketList, ToIntFunction<TT> valueGetter, int defaultMax) {
	return ticketList
		.stream()
		.mapToInt(valueGetter)
		.max()
		.orElse(defaultMax);
    }

    private List<TT> getTickets(TTReader reader) throws Exception {
	return getTicketStreamFromUrl(reader.getUrl())
		.filter(tt -> reader.getFilter().test(tt))
		.filter(this::notAssigned)
		.peek(this::fixUpForDisplay)
		.collect(Collectors.toList());
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
	    weight = (int) Math.pow(intAge, engineExp);
	    break;

	case Constants.ITEM_CUSTOMER_ISSUE:
	    weight += (int) Math.pow(intAge, CUSTOMER_ISSUE_AGE_EXPONENT);
	    break;

	}

	tt.setWeight(weight);
    }

    private Predicate<? super KeywordPoints> keywordMatch(String description) {
	return kw -> foundIn(description, kw.getKeyword());
    }

    private void normalizeWeight(TT tt) {
	tt.setWeight(tt.getWeight() * NORMALIZED_MAXIMUM_WEIGHT / maxCustomerIssueWeight);
    }

    private class CustomerIssueLimter {
	private Map<String, Integer> countMap = new HashMap<>() {
	    private static final long serialVersionUID = 1L;

	    {
		put(Constants.ITEM_CUSTOMER_ISSUE, 0);
		put(Constants.ITEM_ENGINE, 0);
	    }
	};

	public boolean withinLimit(TT tt) {
	    Integer count = countMap.get(tt.getItem());
	    if (count >= MAXIMUM_TICKETS_PER_TYPE) {
		return false;
	    } else {
		count++;
		countMap.put(tt.getItem(), count);
		return true;
	    }
	}

    }

}
