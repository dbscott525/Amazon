package com.amazon.amsoperations.utility;

public class SetUpCIT extends Utility {

    public static void main(String[] args) {
	new SetUpCIT().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(

		CreateTicketClosureReport.class,
		CreateCITResolvedTicketsTable.class,
		CreateTicketFlowReport.class,
		UpdateMasterListWithTicketsClosed.class,
		CreateCSVSchedule.class,
		CreateCITOnlineSchedule.class,
		CreateCITEmails.class,
		CreateCITWeekData.class,
		OpenCurrentWeekCITDocuments.class,
		OpenNextWeekCITDocuments.class

	);

    }

}
