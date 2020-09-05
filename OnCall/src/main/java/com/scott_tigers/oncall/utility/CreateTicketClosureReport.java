package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateTicketClosureReport extends Utility {

    public static void main(String[] args) throws Exception {
	new CreateTicketClosureReport().run();
    }

    private void run() throws Exception {
	writeCSV(EngineerFiles.TICKET_CLOSURES, EngineerMetric.class, getTicketClosedMetrics());
    }

}
