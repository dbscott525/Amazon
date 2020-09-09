package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class LauchCITMidweekDocuments extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new LauchCITMidweekDocuments().run();
    }

    @Override
    public void run() throws Exception {
	EngineerFiles.CIT_WEEK_WELCOME.launch();
	EngineerFiles.ON_CALL_DAILY_REMINDER_EMAIL.launch();
    }

}
