package com.scott_tigers.oncall.utility;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CustomerIssueReader extends Utility implements TTReader {
//    private static final String RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=06%2F01%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21#";
    private static final String RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date=&tags=&case_type=&building_id=&search=Search%21";

    @Override
    public String getUrl() {
	return RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL;
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> {

	    switch (tt.getStatus()) {

	    case "Pending Pending Software Update":
	    case "Pending Verification of fix":
	    case "Pending Daytime Sev2 - Re-assign":
		return true;

	    case "Pending Any Info - 7 Day Auto Resolve":
	    case "Pending Requester Info - 7 Day Auto Resolve":
		return false;

	    }

	    boolean companyMatch = Stream
		    .of(EngineerFiles.TOP_100_COMPANIES, EngineerFiles.ESCALATED_COMPANIES)
		    .flatMap(companyFile -> getCompanyList(companyFile)
			    .stream())
		    .anyMatch(company -> foundIn(tt.getDescription(), company));

	    int age = Integer.parseInt(tt.getAge());

	    if (companyMatch && age < 45) {
		return true;
	    }

	    switch (tt.getStatus()) {

	    case "Pending Pending Customer Response":
		return age > 7;

	    default:
		return age < 60;

	    }
	};
    }

}
