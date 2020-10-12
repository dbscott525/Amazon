package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.Properties;

public class TicketEscalation {
    private String date;
    private String type;
    private String uid;
    private String ticket;
    private String company;
    private String rationale;

    @JsonProperty(Properties.DATE)
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @JsonProperty(Properties.TYPE)
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @JsonProperty(Properties.UID)
    public String getUid() {
	return uid;
    }

    public void setUid(String uid) {
	this.uid = uid;
    }

    @JsonProperty(Properties.TICKET)
    public String getTicket() {
	return ticket;
    }

    public void setTicket(String ticket) {
	this.ticket = ticket;
    }

    @JsonProperty(Properties.COMPANY)
    public String getCompany() {
	return company;
    }

    public void setCompany(String company) {
	this.company = company;
    }

    public String getRationale() {
	return rationale;
    }

    @JsonProperty(Properties.RATIONALE)
    public void setRationale(String rationale) {
	this.rationale = rationale;
    }

    public void normalizeDate() {
	date = Dates.TT_SEARCH.convertFormat(date, Dates.SORTABLE);
    }
}
