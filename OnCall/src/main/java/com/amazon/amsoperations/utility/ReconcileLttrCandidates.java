package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.LTTRTicket;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

public class ReconcileLttrCandidates extends Utility {

    private static final boolean INCLUDE = false;

    public static void main(String[] args) {
	new ReconcileLttrCandidates().run();
    }

    private void run() {
	if (INCLUDE) {
	    List<LTTRTicket> quipTickets = readFromUrl(URL.LTTR_CANDIDATES, LTTRTicket.class)
		    .collect(Collectors.toList());
	}

	Map<String, LTTRTicket> quipMap = getTicketMap(
		() -> readFromUrl(URL.LTTR_CANDIDATES, LTTRTicket.class)
			.collect(Collectors.toList()),
		LTTRTicket::isNotTotal);
	Map<String, LTTRTicket> csvMap = getTicketMap(
		() -> EngineerFiles.LTTR_PLAN_TICKETS.readCSVToPojo(LTTRTicket.class), LTTRTicket::isCandidate);

	notFound("Quip", quipMap, "CSV", csvMap);

    }

    private void notFound(String name1, Map<String, LTTRTicket> map1, String name2,
	    Map<String, LTTRTicket> map2) {
	notFoundx(name1, map1, name2, map2);
	notFoundx(name2, map2, name1, map1);
    }

    private void notFoundx(String name1, Map<String, LTTRTicket> map1, String name2, Map<String, LTTRTicket> map2) {
	System.out.println("Tickets in "
		+ name1 + " not found in " + name2 + ":");

	map1.values()
		.stream()
		.map(LTTRTicket::getTicket)
		.filter(ticket -> map2.get(ticket) == null)
//		.peek(ticket -> {
//		    System.out.println("map2.get(ticket)=" + (map2.get(ticket)));
//		    Json.print(map2);
//		    System.exit(1);
//		})
		.forEach(System.out::println);
    }

    private Map<String, LTTRTicket> getTicketMap(Supplier<List<LTTRTicket>> reader, Predicate<LTTRTicket> filter) {
	Map<String, LTTRTicket> csvMap = reader.get()
		.stream()
		.filter(filter)
		.collect(Collectors.toMap(LTTRTicket::getTicket, Function.identity()));
	return csvMap;
    };
}
