package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class CreateLttrWeekDeltaReport extends Utility {

    public static void main(String[] args) {
	new CreateLttrWeekDeltaReport().run();
    }

    private Map<String, LTTRTicket> lastWeek;
    private Map<String, LTTRTicket> penultimateWeek;

    private void run() {
	lastWeek = LTTRPage.LAST_FULL_WEEK.getMap();
	penultimateWeek = LTTRPage.PENULTIMATE_FULL_WEEK.getMap();
	Stream<LTTRTicket> newStream = lastWeekStream()
		.filter(tkt -> penultimateWeek.get(tkt.getTicket()) == null)
		.peek(tkt -> tkt.setDelta(tkt.getTickets()))
		.peek(tkt -> tkt.setType("New"));

	Stream<LTTRTicket> moreTickets = getDeltaStream(this::moreTickets);
	Stream<LTTRTicket> lessTickets = getDeltaStream(this::lessTickets);

	List<LTTRTicket> tickets = Stream
		.of(newStream, moreTickets, lessTickets)
		.flatMap(x -> x)
		.sorted(byDelta())
		.collect(Collectors.toList());

	EngineerFiles.LTTR_WEEK_DELTA_REPORT.write(
		w -> w.CSV(tickets, Properties.TYPE, Properties.DELTA, Properties.TICKET, Properties.DESCRIPTION));
	launchUrl(URL.LTTR_TICKETS_LAST_WEEK_DELTA_REPORT);
    }

    private Stream<LTTRTicket> getDeltaStream(Function<LTTRTicket, LTTRTicket> mapper) {
	Stream<LTTRTicket> moreTickets = lastWeekStream()
		.map(mapper)
		.filter(Objects::nonNull);
	return moreTickets;
    }

    private Stream<LTTRTicket> lastWeekStream() {
	return lastWeek
		.values()
		.stream();
    }

    private Comparator<LTTRTicket> byDelta() {
	return (t1, t2) -> {
	    int compare = t1.getType().compareTo(t2.getType());

	    if (compare != 0) {
		return compare;
	    }

	    return t2.getDeltaInteger().compareTo(t1.getDeltaInteger());
	};
    }

    private LTTRTicket lessTickets(LTTRTicket ticket) {
	return getDeltaTicket(ticket, "Decrease", (v1, v2) -> v2 - v1);
    }

    private LTTRTicket moreTickets(LTTRTicket ticket) {
	return getDeltaTicket(ticket, "Increase", (v1, v2) -> v1 - v2);
    }

    private LTTRTicket getDeltaTicket(LTTRTicket ticket, String type,
	    BiFunction<Integer, Integer, Integer> delaComputer) {
	LTTRTicket penTicket = penultimateWeek.get(ticket.getTicket());

	if (penTicket == null) {
	    return null;
	}

	int delta = delaComputer.apply(ticket.getIntTickets(), penTicket.getIntTickets());

	if (delta <= 0) {
	    return null;
	}
	ticket.setDelta("" + delta);
	ticket.setType(type);

	return ticket;
    }
}
