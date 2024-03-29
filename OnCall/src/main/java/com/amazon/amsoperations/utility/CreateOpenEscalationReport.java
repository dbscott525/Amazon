package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.bean.TicketEscalation;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Properties;
import com.amazon.amsoperations.shared.Status;
import com.amazon.amsoperations.shared.URL;
import com.amazon.amsoperations.shared.Util;

public class CreateOpenEscalationReport extends Utility {

    public static void main(String[] args) throws Exception {
	new CreateOpenEscalationReport().run();
    }

    private void run() throws Exception {

	List<TicketEscalation> escalationsFromQuip = readFromUrl(URL.TICKET_ESCALATIONS, TicketEscalation.class)
		.peek(escalation -> escalation.canonicalize())
		.collect(Collectors.toList());

	checkForDuplicates(escalationsFromQuip);

	Map<Integer, TicketEscalation> quipMap = escalationsFromQuip.stream()
		.collect(Collectors.toMap(x -> Util.getCaseId(x.getTicket()), Function.identity()));

	Map<Integer, TT> escalatedTicketsMap = getTicketStreamFromUrl(URL.ESCALATED_TICKETS)
//		.filter(tt -> tt.getDescription().toLowerCase().contains("escalation]"))
//		.filter(tt -> Status.getStatus(tt).isUnresolved())
		.collect(Collectors.toMap(TT::getIntCaseId, Function.identity()));

	escalationsFromQuip.stream().forEach(ticket -> {
	    String ticketUrl = ticket.getTicket();
	    Integer caseId = Util.getCaseId(ticketUrl);
	    TT tt = escalatedTicketsMap.get(caseId);
	    if (tt != null) {
		ticket.setDescription(tt.getDescription());
		ticket.setStatus(tt.getStatus().replace("Pending Pending", "Pending"));
		ticket.setLastModifiedDate(Dates.TT_DATE.convertFormat(tt.getLastModifiedDate(), Dates.TIME_STAMP));
	    } else {
		ticket.setStatus(Status.CLOSED.getValue());
	    }
	});

	Stream<TicketEscalation> missedTicketsStream = escalatedTicketsMap
		.values()
		.stream()
		.filter(tt -> Status.getStatus(tt).isUnresolved())
		.filter(tt -> quipMap.get(tt.getIntegerCaseId()) == null)
		.map(tt -> new TicketEscalation(tt));

	List<TicketEscalation> combinedList = Stream.concat(escalationsFromQuip.stream(), missedTicketsStream)
		.collect(Collectors.toList());

	EngineerFiles.ESCALATED_TICKETS
		.write(w -> w.CSV(combinedList, Properties.LAST_MODIFIED_DATE, Properties.DATE, Properties.TYPE,
			Properties.ESCALATION_BY, Properties.SDE, Properties.TICKET, Properties.DESCRIPTION,
			Properties.COMPANY, Properties.STATUS, Properties.STATE));

    }

    private void checkForDuplicates(List<TicketEscalation> escalationsFromQuip) {
	List<Integer> caseIdsWithDuplicate = escalationsFromQuip
		.stream()
		.map(escalation -> Util.getCaseId(escalation.getTicket()))
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet()
		.stream()
		.filter(entry -> entry.getValue() > 1)
		.map(Entry<Integer, Long>::getKey)
		.collect(Collectors.toList());

	if (caseIdsWithDuplicate.size() > 0) {
	    System.out.println("duplicate case id's: " + caseIdsWithDuplicate);
	    System.exit(1);
	}
    }

}
