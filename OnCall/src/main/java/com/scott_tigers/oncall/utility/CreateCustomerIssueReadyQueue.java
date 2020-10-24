package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.KeywordPoints;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.SpecialLabel;
import com.scott_tigers.oncall.shared.TicketType;

public class CreateCustomerIssueReadyQueue extends Utility {

    private static final int NORMALIZED_MAXIMUM_WEIGHT = 100;
    private static final String[] READY_QUEUE_COLUMNS = {
	    Properties.ITEM,
	    Properties.URL,
	    Properties.CREATE_DATE,
	    Properties.WEIGHT,
	    Properties.DESCRIPTION };

    private List<KeywordPoints> keywordPoints;
    private int maxCustomerIssueWeight;
    private List<TT> topTickets;
    private double engineTypeExponent;
    private WeightContext weightContext = new WeightContext();

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

	List<TT> engineTickets = getTickets(new CreateRootCauseToDoList());
	int maxEnginerAge = getMaximum(engineTickets, TT::getIntAge, 0);

	engineTypeExponent = Math.log(maxCustomerIssueWeight * .6) / Math.log(maxEnginerAge);
	weightContext.setEngineTypeExponent(engineTypeExponent);

	Stream<TT> engineStream = engineTickets.stream().peek(this::fixUpForDisplay);
	Stream<TT> customerIssueStream = ciTickets.stream();

	topTickets = Stream.concat(engineStream, customerIssueStream)
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
	wip = 0;
	AtomicInteger candidateTickets = new AtomicInteger();

	List<TT> tickets = getTicketStreamFromUrl(reader.getUrl())
		.peek(x -> candidateTickets.incrementAndGet())
		.filter(tt -> reader.getFilter().test(tt))
		.filter(this::notAssigned)
		.peek(this::fixUpForDisplay)
		.collect(Collectors.toList());
	System.out.println(reader.getTitle() + ":");
	System.out.println("Candidate Tickets: " + (candidateTickets));
	System.out.println("Work in Progress: " + (wip));
	System.out.println("Backlog: " + tickets.size());
	reader.printReport();
	return tickets;
    }

    private void readPointData() {
	keywordPoints = EngineerFiles.KEYWORD_POINTS
		.readCSVToPojo(KeywordPoints.class);
	weightContext.setKeywordPoints(keywordPoints);
    }

    private void fixUpForDisplay(TT tt) {
	weightContext.setTt(tt);
	tt.setWeight(TicketType
		.getType(tt.getItem())
		.getWeight(weightContext));
    }

    private void normalizeWeight(TT tt) {
	tt.setWeight(tt.getWeight() * NORMALIZED_MAXIMUM_WEIGHT / maxCustomerIssueWeight);
	Stream
		.of(SpecialLabel.values())
		.filter(SpecialLabel.match(tt.getDescription()))
		.findFirst()
		.ifPresent(specialLabel -> tt.setItem(specialLabel.toString()));
    }

    private class CustomerIssueLimter {
	private Map<TicketType, Integer> countMap = new HashMap<>();

	public boolean withinLimit(TT tt) {
	    TicketType type = TicketType.getType(tt.getItem());
	    Integer count = Optional.ofNullable(countMap.get(type)).orElse(0);
	    if (count >= type.getMaximumTickets()) {
		return false;
	    } else {
		count++;
		countMap.put(type, count);
		return true;
	    }
	}

    }

}
