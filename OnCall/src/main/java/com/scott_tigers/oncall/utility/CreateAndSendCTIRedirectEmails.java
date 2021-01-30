package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.CTIRedirect;
import com.scott_tigers.oncall.bean.CTIRedirectEmail;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class CreateAndSendCTIRedirectEmails extends Utility {

    private static final String CURRENT_MARKER = "CURRENT";
    private static final String PREVIOUS_MARKER = "PREVIOUS";
    private static final String DATE_REPLACEMENT_REGEX = "(.*?)" + PREVIOUS_MARKER + "(.*?)" + CURRENT_MARKER + "(.*?)";
    private static final String SEARCH_URL_TEMPLATE = "https://tt.amazon.com/"
	    + "search?category=AWS&type=RDS-AuroraMySQL&item=Engine&"
	    + "assigned_group=&"
	    + "status=Resolved%3BClosed&"
	    + "impact=&"
	    + "assigned_individual=&requester_login=&login_name=&cc_email=&"
	    + "phrase_search_text=&keyword_bq=&exact_bq=&or_bq1=&or_bq2=&or_bq3=&exclude_bq=&create_date="
	    + PREVIOUS_MARKER
	    + "%2C"
	    + CURRENT_MARKER
	    + "&modified_date=&tags=&case_type=&building_id=&search=Search%21";

    public static void main(String[] args) throws Exception {
	new CreateAndSendCTIRedirectEmails().run();
    }

    private List<CTIRedirect> redirects;
    private Map<String, Engineer> engineerMap;

    private void run() throws Exception {
	engineerMap = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.collect(Collectors.toMap(x -> x.getUid(), x -> x));

	redirects = EngineerFiles.CTI_REDIRECT_SIMS.readCSVToPojo(CTIRedirect.class);
	redirects.stream().map(CTIRedirect::getSim).forEach(System.out::println);

	List<CTIRedirectEmail> redirectEmailData = getTicketStreamFromUrl(getSearchURL())
		.filter(this::isRedirect)
		.map(tt -> toRedirectEmail(tt))
		.filter(Objects::nonNull)
		.collect(Collectors.toList());

	Json.print(redirectEmailData);

	if (redirectEmailData.isEmpty()) {
	    System.out.println("No redirects found");
	    EngineerFiles.NO_TICKETS_FOUND.launch();
	} else {
	    EngineerFiles.CTI_ASSIGNMENT_REMINDER_DATA.write(w -> w.CSV(redirectEmailData, CTIRedirectEmail.class));
	    EngineerFiles.CTI_ASSIGNMENT_REMINDER_EMAIL.launch();

	    Stream<Function<CTIRedirectEmail, String>> stream = Stream.of(
		    CTIRedirectEmail::getSim,
		    CTIRedirectEmail::getTt);

	    stream
		    .flatMap(mapper -> redirectEmailData
			    .stream()
			    .map(mapper::apply))
		    .distinct()
		    .forEach(this::launchUrl);
	}

    }

    private String getSearchURL() {
	String regex = DATE_REPLACEMENT_REGEX;
	String currentDate = Dates.TT_SEARCH.getFormattedDelta(Dates.TT_SEARCH.getFormattedDate(), 1);
	String previousDate = Dates.TT_SEARCH.getFormattedDelta(currentDate, -8);
	String replacement = "$1" + previousDate + "$2" + currentDate + "$3";
	String searchURL = SEARCH_URL_TEMPLATE.replaceAll(regex, replacement);
	return searchURL;
    }

    private CTIRedirectEmail toRedirectEmail(TT tt) {
	CTIRedirect simRedirect = redirects.stream()
		.filter(rd -> tt
			.getRootCauseDetails()
			.toLowerCase()
			.contains(rd.getSim().toLowerCase()))
		.findFirst()
		.orElse(null);

	if (simRedirect == null) {
	    return null;
	}

	CTIRedirectEmail redirect = new CTIRedirectEmail();
	boolean resolvedByMinions = "flx-AuroraOps-Minions".equals(tt.getResolvedBy());
	String uid = resolvedByMinions ? tt.getLastModifiedBy() : tt.getResolvedBy();
	redirect.setEmail(uid + "@amazon.com");
	String name = Optional
		.ofNullable(engineerMap.get(uid))
		.map(eng -> eng.getFirstName())
		.orElse("unknown");
	redirect.setName(name);
	redirect.setSim("https://issues.amazon.com/" + simRedirect.getSim());
	redirect.setTt(tt.getUrl());

	return redirect;
    }

    private boolean isRedirect(TT tt) {
	return redirects.stream()
		.anyMatch(rd -> tt.getRootCauseDetails().toLowerCase().contains(rd.getSim().toLowerCase()));
    }
}
