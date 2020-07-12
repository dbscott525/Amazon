package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.Map.Entry;

public class TicketStatusCount {
    private String status;
    private Integer count;

    public TicketStatusCount(Entry<String, List<TT>> entry) {
	status = entry.getKey().replace("Pending Pending", "Pending");
	count = entry.getValue().size();
    }

    public TicketStatusCount() {
    }

    public String getStatus() {
	return status;
    }

    public Integer getCount() {
	return count;
    }

}
