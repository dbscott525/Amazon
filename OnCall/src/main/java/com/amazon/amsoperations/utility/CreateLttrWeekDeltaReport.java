package com.amazon.amsoperations.utility;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Properties;
import com.amazon.amsoperations.shared.URL;

public class CreateLttrWeekDeltaReport extends Utility implements Command {

    public static void main(String[] args) {
	new CreateLttrWeekDeltaReport().run();
    }

    private Map<String, LTTRTicket> lastWeek;
    private Map<String, LTTRTicket> penultimateWeek;

    public void run() {
	Map<String, String> dispositionMap = Stream.of(LTTRList.values())
		.flatMap(lttrList -> readFromUrl(lttrList.getUrl(), LTTRTicket.class)
			.map(ticket -> new AbstractMap.SimpleEntry<String, String>(ticket.getTicket(),
				lttrList.getDisplayName())))
		.collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));
//	Json.print(dispositionMap);
//	System.exit(1);
	lastWeek = LTTRPage.LAST_FULL_WEEK.getMap();
	penultimateWeek = LTTRPage.PENULTIMATE_FULL_WEEK.getMap();
	lastWeekStream().forEach(tt -> {
	    LTTRTicket pentTT = penultimateWeek.get(tt.getTicket());
	    if (pentTT == null) {
		tt.setDelta(tt.getTickets());
		tt.setType("New");
	    } else {
		tt.setDelta("" + (tt.getIntTickets() - pentTT.getIntTickets()));
		tt.setType("Existed");
	    }
	});
	penultimateWeek
		.values()
		.stream()
		.filter(tt -> lastWeek.get(tt.getTicket()) == null)
		.forEach(tt -> {
		    tt.setDelta("" + (-tt.getIntTickets()));
		});
	List<LTTRTicket> report = Stream
		.of(lastWeek, penultimateWeek)
		.flatMap(map -> map.values().stream())
		.filter(tt -> Objects.nonNull(tt.getDelta()))
		.filter(tt -> Math.abs(Integer.parseInt(tt.getDelta())) > 3)
		.peek(tt -> tt.setArea(dispositionMap.get(tt.getTicket())))
		.sorted(Comparator.comparing(t -> -Integer.parseInt(t.getDelta())))
		.collect(Collectors.toList());

//	Json.print(lastWeek);
//	Stream<LTTRTicket> newStream = lastWeekStream()
//		.filter(tkt -> penultimateWeek.get(tkt.getTicket()) == null)
//		.peek(tkt -> tkt.setDelta(tkt.getTickets()))
//		.peek(tkt -> tkt.setType("New"));
//
//	Stream<LTTRTicket> moreTickets = getDeltaStream(this::moreTickets);
//	Stream<LTTRTicket> lessTickets = getDeltaStream(this::lessTickets);
//
//	List<LTTRTicket> tickets = Stream
//		.of(newStream, moreTickets, lessTickets)
//		.flatMap(x -> x)
//		.sorted(byDelta())
//		.collect(Collectors.toList());
//
	EngineerFiles.LTTR_WEEK_DELTA_REPORT.write(
		w -> w.CSV(report, Properties.TYPE, Properties.DELTA, Properties.TICKET, Properties.DESCRIPTION,
			Properties.AREA));
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
//	return getDeltaTicket(ticket, "Decrease", (v1, v2) -> v1 - v2);
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
