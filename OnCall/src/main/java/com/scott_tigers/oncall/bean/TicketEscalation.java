package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;
import com.scott_tigers.oncall.shared.Util;

public class TicketEscalation {
    private String date;
    private String type;
    private String escalatedBy;
    private String sde;
    private String ticket;
    private String description;
    private String company;
    private String status;
    private String state;
    private String rationale;
    @SuppressWarnings("unused")
    private String email;
    private String lastModifiedDate;

    public TicketEscalation(TT tt) {
	ticket = tt.getUrl();
	description = tt.getDescription();
	status = tt.getStatus();
	lastModifiedDate = tt.getLastModifiedDate();
    }

    public TicketEscalation() {
    }

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

    @JsonProperty(Properties.ESCALATION_BY)
    public String getEscalatedBy() {
	return escalatedBy;
    }

    public void setEscalatedBy(String escalatedBy) {
	this.escalatedBy = escalatedBy;
    }

    @JsonProperty(Properties.SDE)
    public String getSde() {
	return sde;
    }

    public void setSde(String sdeUid) {
	this.sde = sdeUid;
    }

    @JsonProperty(Properties.TICKET)
    public String getTicket() {
	return ticket;
    }

    public void setTicket(String ticket) {
	this.ticket = ticket;
    }

    @JsonProperty(Properties.DESCRIPTION)
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @JsonProperty(Properties.COMPANY)
    public String getCompany() {
	return company;
    }

    @JsonProperty(Properties.STATUS)
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    @JsonProperty(Properties.STATE)
    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public void setCompany(String company) {
	this.company = company;
    }

    @JsonProperty(Properties.RATIONALE)
    public String getRationale() {
	return rationale;
    }

    public void setRationale(String rationale) {
	this.rationale = rationale;
    }

    @JsonProperty(Properties.LAST_MODIFIED_DATE)
    public String getLastModifiedDate() {
	return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
    }

    @JsonProperty(Properties.EMAIL)
    public String getEmail() {
	return "aurora-mysql-new-escalation-notice@amazon.com";
    }

    public void normalizeDate() {
	date = Dates.TT_SEARCH.convertFormat(date, Dates.SORTABLE);
    }

    public void canonicalize() {
	ticket = URL.TT_URL_PREFIX + Util.getCaseId(ticket);
    }
}
