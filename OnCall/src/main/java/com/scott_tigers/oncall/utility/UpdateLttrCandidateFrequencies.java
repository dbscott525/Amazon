package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.URL;

public class UpdateLttrCandidateFrequencies extends UpdateLttrTicketFrequencies {

    public static void main(String[] args) {
	new UpdateLttrCandidateFrequencies().run();
    }

    @Override
    protected void run() {
	super.run();
    }

    @Override
    protected Stream<LTTRTicket> getQuipLttrTicketStream() {
	return readFromUrl(URL.LTTR_CANDIDATES, LTTRTicket.class);
    }
}
