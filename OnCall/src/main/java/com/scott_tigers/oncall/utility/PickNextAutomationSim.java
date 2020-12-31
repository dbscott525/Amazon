package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;

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

}
