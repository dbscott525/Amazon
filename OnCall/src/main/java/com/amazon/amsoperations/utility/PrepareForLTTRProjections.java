package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

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
