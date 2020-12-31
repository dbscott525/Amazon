package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class OpenNextWeekCITDocuments extends Utility implements Command {


    public static void main(String[] args) throws Exception {
	new OpenNextWeekCITDocuments().run();
    }

    @Override
    public void run() throws Exception {
	EngineerFiles.CIT_WEEK_WELCOME.launch();
	EngineerFiles.CIT_LAST_DAY_EMAIL.launch();
	EngineerFiles.CIT_DAY_BEFORE_EMAIL.launch();
	launchUrl(URL.CIT_ON_CALL_SCHEDULE);
	launchUrl(URL.CIT_SCHEDULE);
    }

}
