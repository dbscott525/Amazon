package com.amazon.amsoperations.utility;

public class PrepareForNextCITEndOfWeek extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCITEndOfWeek().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		UpdateMasterListWithTicketsClosed.class,
		CreateCITWeekData.class,
		CreateCITResolvedTicketsTable.class,
		CreateTicketFlowReport.class,
		CreateTicketClosureReport.class,
		OpenCurrentWeekCITDocuments.class);
    }

}
