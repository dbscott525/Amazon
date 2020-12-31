package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;

public abstract class ReconcileLTTRTickets extends Utility {

    protected void run() {

	List<String> quipTickets = getQuipTickets()
		.map(LTTRTicket::getTicket)
		.filter(this::include)
		.collect(Collectors.toList());

	List<String> csvTickets = EngineerFiles.LTTR_PLAN_TICKETS
		.readCSVToPojo(LTTRTicket.class)
		.stream()
		.filter(tkt -> tkt.getState().equals(getState()))
		.map(LTTRTicket::getTicket)
		.filter(this::include)
		.collect(Collectors.toList());

	compare("quip", quipTickets, "CSV", csvTickets);

    }

    protected abstract String getState();

    protected abstract Stream<LTTRTicket> getQuipTickets();

    private boolean include(String ticket) {
	return Stream
		.of("22276", "10335", "15630")
		.noneMatch(ticket::contains);
    }

    private void compare(String title1, List<String> list1, String title2,
	    List<String> list2) {
	compareContains(title1, list1, title2, list2);
	compareContains(title2, list2, title1, list1);
    }

    private void compareContains(String title1, List<String> list1, String title2,
	    List<String> list2) {

	System.out.println("Tickets in " + title1 + " but not in " + title2);
	list1.stream().filter(ticket -> !list2.contains(ticket)).forEach(tkt -> System.out.println("tkt=" + (tkt)));
    }
}
