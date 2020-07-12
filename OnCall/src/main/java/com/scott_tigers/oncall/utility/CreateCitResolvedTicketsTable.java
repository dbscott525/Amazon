package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TicketStatusCount;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCitResolvedTicketsTable extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateCitResolvedTicketsTable().run();
    }

    private void run() throws IOException {

	copyMostRecentDownloadedTTs();
	List<TicketStatusCount> resolvedTickets = EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream()
		.filter(tt -> !tt.getStatus().equals("Pending Pending Root Cause"))
		.collect(Collectors.groupingBy(TT::getStatus))
		.entrySet()
		.stream()
		.map(TicketStatusCount::new)
		.collect(Collectors.toList());

	EngineerFiles.RESOLVED_TICKET_SUMMARY.writeCSV(resolvedTickets, TicketStatusCount.class);

	successfulFileCreation(EngineerFiles.RESOLVED_TICKET_SUMMARY);
    }

}
