package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class OpenOpsReviewDocuments extends Utility {

    public static void main(String[] args) {
	new OpenOpsReviewDocuments().run();
    }

    private void run() {
	EngineerFiles.TIME_TO_CLOSE.launch();
	EngineerFiles.TIME_TO_CLOSE_GRAPH.launch();
	EngineerFiles.TICKET_FLOW_REPORT.launch();
	EngineerFiles.TICKET_FLOW_GRAPH_WITH_OPENED.launch();
	launchUrl(LTTRPage.GRAPH.getUrl());
	launchUrl(LTTRPage.LAST_FULL_WEEK.getUrl());
	Stream.of(

		URL.LTTR_TICKETS_LAST_WEEK_DELTA_REPORT,
		URL.LTTR_PLAN,
		URL.LTTR_CANDIDATES

	)
		.forEach(this::launchUrl);
    }
}
