package com.scott_tigers.oncall.test;

import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Status;
import com.scott_tigers.oncall.utility.Utility;

public class TestStatus extends Utility {

    public static void main(String[] args) throws Exception {
	new TestStatus().run();
    }

    private void run() throws Exception {
	Stream
		.of(
			"Pending Any Info - 7 Day Auto Resolve",
			"Pending Pending Customer Response",
			"Pending Pending Root Cause",
			"Pending Pending Software Update",
			"Pending Requester Info - 7 Day Auto Resolve",
			"Pending Daytime Sev2 - Re-assign",
			"Pending Verification of fix",
			"Pending Requester Info - 3 Day Auto Resolve",
			"bogus")
		.map(Status::get)
		.forEach(status -> System.out.println(status
			+ ":"
			+ (status.isAlwaysInQueue() ? " [is always in queue]" : "")
			+ (status.isNeverInQueue() ? " [is never in queue]" : "")
			+ (status.includeInSummary() ? " [include in summary]" : "")));
    }
}