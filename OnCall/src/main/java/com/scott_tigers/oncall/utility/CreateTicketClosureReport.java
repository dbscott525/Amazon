package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateTicketClosureReport extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateTicketClosureReport().run();
    }

    @Override
    public void run() throws Exception {

	EngineerFiles.TICKET_CLOSURES.write(w -> w
		.fileType(EngineerFiles.TICKET_CLOSURES)
		.CSV(getTicketClosedMetrics(), EngineerMetric.class));

	waitForDataFileLaunch();

	EngineerFiles.TICKET_CLOSURE_BAR_GRAPH.launch();
    }

}
