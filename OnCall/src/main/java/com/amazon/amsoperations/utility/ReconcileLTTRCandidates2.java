package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;

public class ReconcileLTTRCandidates2 extends ReconcileLTTRTickets {

    public static void main(String[] args) {
	new ReconcileLTTRCandidates2().run();
    }

    @Override
    protected Stream<LTTRTicket> getQuipTickets() {
	return getLTTRCandidates();
    }

    @Override
    protected String getState() {
	return "Candidate";
    }
}
