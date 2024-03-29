package com.amazon.amsoperations.utility;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.bean.TTReader;
import com.amazon.amsoperations.shared.Status;
import com.amazon.amsoperations.shared.URL;

public class CustomerIssueReader extends Utility implements TTReader {
    private int statusExcludedFromQueue = 0;
    private int excludedByAge = 0;

    @Override
    public String getUrl() {
	return URL.RECENT_OPEN_CUSTOMER_ISSUE_SEARCH;
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> {

//	    if (tt.getDescription().contains("Instance Unhealthy")) {
//		return false;
//	    }

	    Status status = Status.get(tt.getStatus());

	    if (status.isAlwaysInQueue()) {
		return true;
	    }

	    if (status.isNeverInQueue()) {
		statusExcludedFromQueue++;
		return false;
	    }

	    int age = Integer.parseInt(tt.getAge());

	    boolean ageIsOK = status.agedLongEnough(age);

	    if (!ageIsOK) {
		excludedByAge++;
		System.out.println(tt.getUrl() + ": age=" + age);
	    }
	    return ageIsOK;
	};
    }

    @Override
    public String getTitle() {
	return "CustomerIssue";
    }

    @Override
    public void printReport() {
	System.out.println("Auto Resolve: " + (statusExcludedFromQueue));
	System.out.println("Too Early: " + (excludedByAge));
    }

    @Override
    public Stream<TT> getTicketStream() throws Exception {
	return getTicketStreamFromUrl(getUrl());
    }

}
