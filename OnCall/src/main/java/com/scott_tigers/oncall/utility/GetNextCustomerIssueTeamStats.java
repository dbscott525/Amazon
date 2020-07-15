package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class GetNextCustomerIssueTeamStats extends Utility {

    public static void main(String[] args) throws Exception {
	new GetNextCustomerIssueTeamStats().run();
    }

    private void run() throws Exception {
	String url = "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=CustomerIssue&assigned_group=aurora-head%3Baurora-head-trx%3Baurora-head-backlog%3Baurora-head-ecosystem%3Baurora-head-partition%3Baurora-head-qp%3Baurora-head-store%3Baurora-head-secondary-wip%3Boscar-eng-secondary%3Baurora-secondary-RCA%3Baurora-head-serverless&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date=03%2F02%2F2020%2C03%2F30%2F2020&modified_date=&tags=&case_type=&building_id=&search=Search%21";
	Stream<TT> tt1 = getTicketStreamFromUrl(url);
	Stream<TTStat> tt2 = tt1.map(tt -> new TTStat(tt));
	List<TTStat> tt3 = tt2.collect(Collectors.toList());
	Json.print(tt3);
	EngineerFiles.TICKET_STATS.writeCSV(tt3, TTStat.class);

	successfulFileCreation(EngineerFiles.TICKET_STATS);
    }

}
