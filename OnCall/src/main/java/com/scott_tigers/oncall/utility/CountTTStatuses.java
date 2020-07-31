package com.scott_tigers.oncall.utility;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.TT;

public class CountTTStatuses extends Utility {

    public static void main(String[] args) throws Exception {
	new CountTTStatuses().run();
    }

    private void run() throws Exception {
	String url = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=&modified_date=&tags=&case_type=&building_id=&search=Search%21";
	Stream<TT> x1 = getTicketStreamFromUrl(url);
	Map<String, Long> x2 = x1.collect(Collectors.groupingBy(TT::getStatus, Collectors.counting()));

	x2.entrySet().forEach(entry -> System.out.println(entry.getValue()
		+ " - "
		+ entry.getKey()));
	x2.entrySet().forEach(entry -> System.out.println(entry.getKey()));

    }

}