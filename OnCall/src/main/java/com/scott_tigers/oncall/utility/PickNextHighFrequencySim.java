package com.scott_tigers.oncall.utility;

import org.openqa.selenium.WebDriver;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class PickNextHighFrequencySim extends PickNextLttrSim {

    public static void main(String[] args) throws InterruptedException {
	new PickNextHighFrequencySim().run();
    }

    @Override
    protected void run() throws InterruptedException {
	super.run();
	launchUrl(URL.LTTR_CANDIDATES);
	launchUrl(URL.COMPONENT_LABELS);
    }

    @Override
    protected EngineerFiles getTickertPlanFile() {
	return EngineerFiles.LTTR_PLAN_TICKETS;
    }

    @Override
    protected void processTicket(WebDriver driver, LTTRTicket ticket) {
	ticket.setEmail(Constants.REPLACE_ME_EMAIL);
	ticket.setTo("SDM");
	ticket.setState("Candidate");

    }

    @Override
    protected TicketType getTicketType() {
	return TicketType.HIGH_FREQUENCY;
    }

}
