package com.scott_tigers.oncall.utility;

import java.util.function.Predicate;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.TTReader;
import com.scott_tigers.oncall.shared.TicketStatuses;

public class CustomerIssueReader extends Utility implements TTReader {
    private static final int PENDING_CUSTOMER_RESPONSE_WAIT_TIME = 7;
    private static final String RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date=&tags=&case_type=&building_id=&search=Search%21";

    @Override
    public String getUrl() {
	return RECENT_OPEN_CUSTOMER_ISSUE_SEARCH_URL;
    }

    @Override
    public Predicate<TT> getFilter() {
	return tt -> {

	    switch (tt.getStatus()) {

	    case TicketStatuses.PENDING_PENDING_SOFTWARE_UPDATE:
	    case TicketStatuses.PENDING_VERIFICATION_OF_FIX:
	    case TicketStatuses.PENDING_DAYTIME_SEV2_RE_ASSIGN:
		return true;

	    case TicketStatuses.PENDING_ANY_INFO_7_DAY_AUTO_RESOLVE:
	    case TicketStatuses.PENDING_REQUESTER_INFO_7_DAY_AUTO_RESOLVE:
	    case TicketStatuses.PENDING_REQUESTER_INFO_3_DAY_AUTO_RESOLVE:
		return false;

	    }

//	    boolean companyMatch = Stream
//		    .of(EngineerFiles.TOP_100_COMPANIES, EngineerFiles.ESCALATED_COMPANIES)
//		    .flatMap(companyFile -> getCompanyList(companyFile)
//			    .stream())
//		    .anyMatch(company -> foundIn(tt.getDescription(), company));

	    int age = Integer.parseInt(tt.getAge());

//	    if (companyMatch && age < 90) {
//		return true;
//	    }

	    switch (tt.getStatus()) {

	    case TicketStatuses.PENDING_PENDING_CUSTOMER_RESPONSE:
		return age > PENDING_CUSTOMER_RESPONSE_WAIT_TIME;

	    default:
//		return age < MAXIMUM_AGE;
		return true;

	    }
	};
    }

}
