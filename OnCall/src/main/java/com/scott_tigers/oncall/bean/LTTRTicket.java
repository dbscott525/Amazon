package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LTTRTicket {

    private String ticket;
    private String description;
    private String count;
    private String perWeek;
    private String assignee;
    private String nop;
    private String status;

    public String getTicket() {
	return ticket;
    }

    public void setTicket(String ticket) {
	this.ticket = ticket;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCount() {
	return count;
    }

    public void setCount(String count) {
	this.count = count;
    }

    public String getPerWeek() {
	return perWeek;
    }

    public void setPerWeek(String perWeek) {
	this.perWeek = perWeek;
    }

    public String getAssignee() {
	return assignee;
    }

    public void setAssignee(String assignee) {
	this.assignee = assignee;
    }

    public String getNop() {
	return nop;
    }

    public void setNop(String nop) {
	this.nop = nop;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

}
