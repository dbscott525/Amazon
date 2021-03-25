package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.bean.EngineerMetric;
import com.amazon.amsoperations.shared.EngineerFiles;

public class CreateTicketClosureReport extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateTicketClosureReport().run();
    }

    @Override
    public void run() throws Exception {
	System.out.println("Creating Ticket Closure Report");

	EngineerFiles.TICKET_CLOSURES.write(w -> w
		.fileType(EngineerFiles.TICKET_CLOSURES)
		.CSV(getTicketClosedMetrics(), EngineerMetric.class));

	EngineerFiles.TICKET_CLOSURE_BAR_GRAPH.launch();
    }

}
