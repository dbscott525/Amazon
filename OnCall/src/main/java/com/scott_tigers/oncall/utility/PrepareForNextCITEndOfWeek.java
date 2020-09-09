package com.scott_tigers.oncall.utility;

public class PrepareForNextCITEndOfWeek extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCITEndOfWeek().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		UpdateMasterListWithTicketsClosed.class,
		CreateCITOnlineSchedule.class,
		CreateCITWeekData.class,
		CreateCITResolvedTicketsTable.class,
		CreateTicketFlowReport.class,
		CreateTicketClosureReport.class,
		LauchCITEndOfWeekDocuments.class);
    }

}
