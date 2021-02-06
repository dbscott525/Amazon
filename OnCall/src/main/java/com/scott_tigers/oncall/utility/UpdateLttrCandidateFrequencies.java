package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.LTTRTicket;

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
