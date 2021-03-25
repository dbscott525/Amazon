package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;
import com.amazon.amsoperations.shared.Constants;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;
import com.amazon.amsoperations.shared.Util;

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
