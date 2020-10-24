package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TicketEscalation;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class AddTicketEscalation extends Utility {

    public static void main(String[] args) {
	new AddTicketEscalation().run();
    }

    private void run() {

	List<TicketEscalation> newList = readFromUrl(URL.TICKET_ESCALATIONS, TicketEscalation.class)
		.limit(1).collect(Collectors.toList());

	Json.print(newList);

	EngineerFiles.NEW_TICKET_ESCALATION.write(w -> w.CSV(newList, Properties.TYPE, Properties.ESCALATION_BY,
		Properties.TICKET, Properties.COMPANY, Properties.RATIONALE, Properties.EMAIL));
	EngineerFiles.NEW_TICKET_ESCALATION_EMAIL.launch();
    }
}
