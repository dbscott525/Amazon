package com.amazon.amsoperations.utility;

public class MakeCITChanges extends Utility {

    public static void main(String[] args) {
	new MakeCITChanges().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		CreateCSVSchedule.class,
		CreateCITOnlineSchedule.class,
		CreateCITEmails.class,
		OpenNextWeekCITDocuments.class,
		CreateCITWeekData.class,
		OpenCurrentWeekCITDocuments.class,
		OpenNextWeekCITDocuments.class);
    }

}
