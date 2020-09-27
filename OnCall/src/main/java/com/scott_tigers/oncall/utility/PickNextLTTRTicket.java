package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class PickNextLTTRTicket extends Utility {

    public static void main(String[] args) {
	new PickNextLTTRTicket().run();
    }

    private void run() {
	List<LTTRTicket> foo1 = EngineerFiles.LTTR_PRIORITIZED_TICKETS.readCSVToPojo(LTTRTicket.class);

	List<LTTRTicket> foo2 = EngineerFiles.LTTR_PLAN_TICKETS.readCSVToPojo(LTTRTicket.class);
	List<String> planTickets = foo2.stream().map(x -> x.getTicket()).collect(Collectors.toList());
	System.out.println("planTickets=" + (planTickets));

	Optional<String> foo3 = foo1.stream().map(x -> x.getTicket())
		.filter(Predicate.not(t -> planTickets.contains(t))).findFirst();
	foo3.ifPresent(ticket -> {
	    String url = "https://issues.amazon.com/issues/" + ticket;
	    launchUrl(url);
	});
    }

}
