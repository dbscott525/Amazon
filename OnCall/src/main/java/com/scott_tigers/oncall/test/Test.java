package com.scott_tigers.oncall.test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	List<LTTRTicket> tickets = EngineerFiles.LTTR_PLAN_TICKETS.readCSVToPojo(LTTRTicket.class);
	tickets.stream()
		.forEach(tkt -> {
		    String newDes = tkt.getDescription().split("\n")[0];
		    tkt.setDescription(newDes);
		});
	Json.print(tickets);
	EngineerFiles.LTTR_PLAN_TICKETS.write(x -> x.CSV(tickets, LTTRTicket.class));
    }

}