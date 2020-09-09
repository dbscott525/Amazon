package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class LauchCITEndOfWeekDocuments extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new LauchCITEndOfWeekDocuments().run();
    }

    @Override
    public void run() throws Exception {
	EngineerFiles.CIT_DOC_INJECTION_TEMPLATE.launch();
	EngineerFiles.CIT_DSU_TEMPLATE.launch();
	EngineerFiles.CIT_EVALUATION_TEMPLATE.launch();
	EngineerFiles.CUSTOMER_ISSUE_TEAM_INTRODUCTION.launch();
	launchUrl(URL.CIT_TICKET_TRACKER);
	EngineerFiles.CIT_END_OF_WEEK_EMAIL.launch();
    }

}
