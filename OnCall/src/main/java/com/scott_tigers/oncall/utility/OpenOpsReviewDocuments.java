package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.URL;

public class OpenOpsReviewDocuments extends Utility {

    public static void main(String[] args) {
	new OpenOpsReviewDocuments().run();
    }

    private void run() {
	launchUrl(LTTRPage.GRAPH.getUrl());
	Stream.of(

		URL.LTTR_TICKETS_LAST_WEEK_DELTA_REPORT,
		URL.LTTR_PLAN, URL.LTTR_CANDIDATES

	)
		.forEach(this::launchUrl);
    }
}
