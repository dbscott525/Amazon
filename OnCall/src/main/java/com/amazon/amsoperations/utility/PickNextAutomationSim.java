package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

public class PickNextAutomationSim extends PickNextLttrSim {

    public static void main(String[] args) throws InterruptedException {
	new PickNextAutomationSim().run();
    }

    @Override
    protected EngineerFiles getTickertPlanFile() {
	return EngineerFiles.SIM_AUTOMATION_PLAN;
    }

    @Override
    protected void processTicket(LTTRTicket ticket) {
	ticket.setState("Not Automatable");
    }

    @Override
    protected TicketType getTicketType() {
	return TicketType.AUTOMATION;
    }

    @Override
    protected Stream<LTTRTicket> getExistingTicketStream() {
	return readFromUrl(URL.LTTR_AUTOMATION_CANDIDATES, LTTRTicket.class);
    }

    @Override
    protected void doPostSelectionWork() {
	launchUrl(URL.LTTR_AUTOMATION_CANDIDATES);
    }
}
