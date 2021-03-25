package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;
import com.scott_tigers.oncall.shared.Util;

public class PickNextHighFrequencySim extends PickNextLttrSim {

    public static void main(String[] args) throws InterruptedException {
	new PickNextHighFrequencySim().run();
    }

    @Override
    protected void run() throws InterruptedException {
	super.run();
	openLTTRDocuments();
	launchUrl(URL.COMPONENT_LABELS);
	Util.waitForUser("Edit candidate email file, save, close, and hit enter");
	EngineerFiles.LTTR_CANDIDATE_EMAIL.launch();
    }

    @Override
    protected EngineerFiles getTickertPlanFile() {
	return EngineerFiles.LTTR_PLAN_TICKETS;
    }

    @Override
    protected void processTicket(LTTRTicket ticket) {
	ticket.setEmail(Constants.REPLACE_ME_EMAIL);
	ticket.setTo("SDM");
	ticket.setState("Candidate");
	ticket.setSend("Yes");

    }

    @Override
    protected TicketType getTicketType() {
	return TicketType.HIGH_FREQUENCY;
    }

    @Override
    protected Stream<LTTRTicket> getExistingTicketStream() {
	return Stream
		.of(URL.LTTR_CANDIDATES, URL.LTTR_PLAN, URL.LTTR_NON_ACTIONABLE_SIMS)
		.peek(System.out::println)
		.flatMap(url -> readFromUrl(url, LTTRTicket.class));
    }
}
