package com.scott_tigers.oncall.utility;

import java.util.function.Predicate;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.Status;
import com.scott_tigers.oncall.shared.URL;

public class CustomerIssueReader extends Utility implements TTReader {
    @Override
    public String getUrl() {
	return URL.RECENT_OPEN_CUSTOMER_ISSUE_SEARCH;
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> {

	    Status status = Status.get(tt.getStatus());

	    if (status.isAlwaysInQueue()) {
		return true;
	    }

	    if (status.isNeverInQueue()) {
		return false;
	    }

	    int age = Integer.parseInt(tt.getAge());

	    return status.agedLongEnough(age);
	};
    }

}
