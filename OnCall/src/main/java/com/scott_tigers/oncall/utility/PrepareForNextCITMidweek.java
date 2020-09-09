package com.scott_tigers.oncall.utility;

public class PrepareForNextCITMidweek extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCITMidweek().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		UpdateMasterListWithTicketsClosed.class,
		CreateCITEmails.class,
		CreateOncallSchedule.class,
		CreateDailyOnCallReminderEmails.class,
		LauchCITMidweekDocuments.class);
    }

}
