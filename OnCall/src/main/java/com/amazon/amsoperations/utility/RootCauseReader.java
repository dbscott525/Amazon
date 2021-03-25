package com.amazon.amsoperations.utility;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.bean.TTReader;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;

public class RootCauseReader extends Utility implements TTReader {

    @Override
    public String getUrl() {
	String date = Dates.SORTABLE
		.convertFormat(Dates.SORTABLE
			.getFormattedDelta(EngineerFiles.ROOT_CAUSE_TO_DO
				.readCSVToPojo(TT.class)
				.stream()
				.map(TT::getCreateDate)
				.min(Comparator.comparing(String::toString))
				.orElse(getYesterdayDate())
				.substring(0, 10),
				-1),
			Dates.TT_SEARCH);

	return "https://tt.amazon.com/search?category=AWS&type=RDS-AuroraMySQL&item=Engine&assigned_group=aurora-head%3Boscar-eng-secondary&status=Assigned%3BResearching%3BWork+In+Progress%3BPending%3BResolved%3BClosed&impact=&assigned_individual=&requester_login=&login_name=&cc_email=&phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
		+ date
		+ "&modified_date=&tags=&case_type=&building_id=&min_impact=2&search=Search%21#";
    }

    @Override
    public Predicate<TT> getFilter() {
	return null;
    }

    private String getYesterdayDate() {
	return Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -2);
    }

    @Override
    public String getTitle() {
	return "Engine";
    }

    @Override
    public void printReport() {
	// TODO Auto-generated method stub

    }

    @Override
    public Stream<TT> getTicketStream() throws Exception {
	return getTicketStreamFromUrl(getUrl());
    }

}
