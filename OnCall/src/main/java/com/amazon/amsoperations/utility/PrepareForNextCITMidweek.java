package com.amazon.amsoperations.utility;

public class PrepareForNextCITMidweek extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCITMidweek().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		UpdateMasterListWithTicketsClosed.class);

	launchCITUpdater();
    }

}
