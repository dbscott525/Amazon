package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.TicketEscalation;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Json;
import com.amazon.amsoperations.shared.Properties;
import com.amazon.amsoperations.shared.URL;

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
