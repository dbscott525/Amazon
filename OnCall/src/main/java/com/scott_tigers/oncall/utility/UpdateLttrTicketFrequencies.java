package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.WebDriver;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.Util;

public abstract class UpdateLttrTicketFrequencies extends Utility {

    private Map<String, LTTRTicket> lttrMap;

    protected void run() {

	WebDriver driver = Util.getWebDriver();
	lttrMap = LTTRPage.TOP.getMap(driver);
	driver.close();

	List<LTTRTicket> lttrPlanTickets = getQuipLttrTicketStream()
		.peek(ticket -> ticket.update(lttrMap))
		.collect(Collectors.toList());

	EngineerFiles.UPDATED_QUIP_LTTR_PLAN
		.write(w -> w.CSV(lttrPlanTickets, Properties.TICKET, Properties.TICKETS_PER_WEEK,
			Properties.CURRENT_TICKETS_PER_WEEK));
    }

    protected abstract Stream<LTTRTicket> getQuipLttrTicketStream();

}
