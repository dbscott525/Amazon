package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

public class OpenLTTRPlanUpdateFiles extends Utility {

    public static void main(String[] args) {
	new OpenLTTRPlanUpdateFiles().run();
    }

    private void run() {
	EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA.launch();
	EngineerFiles.LTTR_PLAN_TICKETS.launch();
	launchUrl(URL.LTTR_PLAN);
	launchUrl(URL.LTTR_CANDIDATES);
    }

}
