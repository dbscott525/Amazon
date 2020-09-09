package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Properties;

public class TicketStatusCount implements Comparable<TicketStatusCount> {
    private String status;
    private Integer count;

    public TicketStatusCount(Entry<String, List<TT>> entry) {
	status = entry.getKey().replace("Pending Pending", "Pending");
	count = entry.getValue().size();
    }

    public TicketStatusCount() {
    }

    public TicketStatusCount(String status, long count) {
	this.status = status;
	this.count = (int) count;
    }

    @JsonProperty(Properties.STATUS)
    public String getStatus() {
	return status;
    }

    @JsonProperty(Properties.COUNT)
    public Integer getCount() {
	return count;
    }

    @Override
    public int compareTo(TicketStatusCount o) {
	// TODO Auto-generated method stub
	return o.count - count;
    }
}
