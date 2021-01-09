package com.scott_tigers.oncall.utility;

public class ChangeCITSchedule extends Utility {

    public static void main(String[] args) {
	new ChangeCITSchedule().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(

		CreateCSVSchedule.class,
		CreateCITOnlineSchedule.class,
		CreateCITEmails.class,
		CreateCITWeekData.class,
		OpenCurrentWeekCITDocuments.class,
		OpenNextWeekCITDocuments.class

	);

    }
}
