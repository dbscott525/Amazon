package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;

public class UpdateLttrPlanFrequencies extends UpdateLttrTicketFrequencies implements Command {

    public static void main(String[] args) {
	new UpdateLttrPlanFrequencies().run();
    }

    @Override
    public void run() {
	super.run();
    }

    @Override
    protected Stream<LTTRTicket> getQuipLttrTicketStream() {
	return getLttrQuipPlan();
    }
}
