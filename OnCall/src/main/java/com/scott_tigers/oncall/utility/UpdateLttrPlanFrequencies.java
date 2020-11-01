package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;

import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;

public class UpdateLttrPlanFrequencies extends Utility {

    public static void main(String[] args) {
	new UpdateLttrPlanFrequencies().run();
    }

    private Map<String, LTTRTicket> lttrMap;

    private void run() {

	WebDriver driver = getWebDriver();
	lttrMap = getLttrTicketStream(driver)
		.collect(Collectors.toMap(LTTRTicket::getTicket, Function.identity()));
	driver.close();

	List<LTTRTicket> lttrPlanTickets = getLttrQuipPlan()
		.peek(ticket -> ticket.update(lttrMap))
		.collect(Collectors.toList());

	EngineerFiles.UPDATED_QUIP_LTTR_PLAN
		.write(w -> w.CSV(lttrPlanTickets, Properties.TICKET, Properties.TICKETS_PER_WEEK,
			Properties.CURRENT_TICKETS_PER_WEEK));
    }

}
