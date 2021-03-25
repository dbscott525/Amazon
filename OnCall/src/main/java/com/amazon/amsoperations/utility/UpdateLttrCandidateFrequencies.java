package com.amazon.amsoperations.utility;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.LTTRTicket;

public class UpdateLttrCandidateFrequencies extends UpdateLttrTicketFrequencies implements Command {

    public static void main(String[] args) {
	new UpdateLttrCandidateFrequencies().run();
    }

    @Override
    public void run() {
	super.run();
    }

    @Override
    protected Stream<LTTRTicket> getQuipLttrTicketStream() {
	return getLTTRCandidates();
    }
}
