package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;

public class ReconcileLTTRPlans extends ReconcileLTTRTickets {

    public static void main(String[] args) {
	new ReconcileLTTRPlans().run();
    }

    @Override
    protected Stream<LTTRTicket> getQuipTickets() {
        return getLttrQuipPlan();
    }

    @Override
    protected String getState() {
        return "Plan";
    }
}
