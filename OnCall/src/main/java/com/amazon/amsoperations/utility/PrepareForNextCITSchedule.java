package com.amazon.amsoperations.utility;

public class PrepareForNextCITSchedule extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCITSchedule().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(

		UpdateEngineersShiftsAdjustment.class,
		CreateTicketClosureReport.class,
		UpdateMasterListWithTicketsClosed.class,
		UpdateUnavailabilityFromQuip.class

	);
    }

}
