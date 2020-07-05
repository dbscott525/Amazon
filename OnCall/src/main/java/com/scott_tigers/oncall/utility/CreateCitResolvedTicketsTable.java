package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TicketStatusCount;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class CreateCitResolvedTicketsTable extends Utility {

    private static final String RESOLVED_TICKET_SUMMARY = "Resolved Ticket Summary";
    private static final List<String> ENGINE_REVIEW_COLUMNS = Arrays.asList(
	    Properties.LINK,
	    Properties.COMMENTS,
	    Properties.REVIEW,
	    Properties.STATUS,
	    Properties.ROOT_CAUSE_DETAILS,
	    Properties.DESCRIPTION);

    public static void main(String[] args) throws IOException {
	new CreateCitResolvedTicketsTable().run();
    }

    private void run() throws IOException {

	String foo = RESOLVED_TICKET_SUMMARY;
	copyMostRecentDownloadedTTs();
	List<TT> engineTickets = EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream()
		.collect(Collectors.toList());

	Stream<TT> t1 = engineTickets.stream();
	Map<String, List<TT>> t2 = t1.collect(Collectors.groupingBy(TT::getStatus));
	Set<Entry<String, List<TT>>> t3 = t2.entrySet();
	Stream<Entry<String, List<TT>>> t4 = t3.stream();
	Stream<TicketStatusCount> t5 = t4.map(entry -> new TicketStatusCount(entry));
	List<TicketStatusCount> t6 = t5.collect(Collectors.toList());
	EngineerFiles.RESOLVED_TICKET_SUMMARY.writeCSV(t6, TicketStatusCount.class);

//	EngineerFiles.ENGINE_TICKET_DAILY_REVIEW.writeCSV(engineTickets,
//		ENGINE_REVIEW_COLUMNS);
//
	successfulFileCreation(EngineerFiles.RESOLVED_TICKET_SUMMARY);
    }

}
