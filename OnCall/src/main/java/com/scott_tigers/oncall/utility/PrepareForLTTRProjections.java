package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class PrepareForLTTRProjections extends Utility {
    public static void main(String[] args) throws Exception {
	new PrepareForLTTRProjections().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(
		LaunchAMSLTTRGraph.class);
	launchUrl(URL.LTTR_PLAN);
	EngineerFiles.ENGINE_TICKET_COUNTS.launch();
    }

}
