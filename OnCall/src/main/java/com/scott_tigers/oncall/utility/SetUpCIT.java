package com.scott_tigers.oncall.utility;

public class SetUpCIT extends Utility {

    public static void main(String[] args) {
	new SetUpCIT().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		UpdateMasterListWithTicketsClosed.class,
		CreateCITWeekData.class,
		CreateCITResolvedTicketsTable.class,
		CreateTicketFlowReport.class,
		CreateTicketClosureReport.class,
		LauchCITEndOfWeekDocuments.class);

	launchCITUpdater();
    }

}
