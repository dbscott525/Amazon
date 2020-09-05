package com.scott_tigers.oncall.utility;

import java.util.Map;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.URL;

public class CountTTStatuses extends Utility {

    public static void main(String[] args) throws Exception {
	new CountTTStatuses().run();
    }

    private void run() throws Exception {
	Map<String, Long> statusCountMap = getTicketStreamFromUrl(URL.OPEN_CUSTOMER_ISSUE_TICKETS)
		.collect(Collectors.groupingBy(TT::getStatus, Collectors.counting()));

	statusCountMap.entrySet().forEach(entry -> System.out.println(entry.getValue() + " - " + entry.getKey()));
	statusCountMap.entrySet().forEach(entry -> System.out.println(entry.getKey()));

    }

}