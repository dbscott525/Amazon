package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.TicketStatuses;

public class CreateEngineTicketReviewCSV extends Utility {

    private static final List<String> ENGINE_REVIEW_COLUMNS = Arrays.asList(
	    Properties.LINK,
	    Properties.COMMENTS,
	    Properties.REVIEW,
	    Properties.STATUS,
	    Properties.ROOT_CAUSE_DETAILS,
	    Properties.DESCRIPTION);

    public static void main(String[] args) throws IOException {
	new CreateEngineTicketReviewCSV().run();
    }

    private void run() throws IOException {
	// Pending Pending Root Cause

	copyMostRecentDownloadedTTs();
	List<TT> engineTickets = EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream()
		.filter(tt -> !tt.getStatus().equals(TicketStatuses.PENDING_PENDING_ROOT_CAUSE))
		.collect(Collectors.toList());

	EngineerFiles.ENGINE_TICKET_DAILY_REVIEW.writeCSV(engineTickets,
		ENGINE_REVIEW_COLUMNS);

	successfulFileCreation(EngineerFiles.ENGINE_TICKET_DAILY_REVIEW);
    }

}
