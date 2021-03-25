package com.amazon.amsoperations.bean;

import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Properties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WIP {

    private String ticketURL;
    private String date;
    private String owner;

    @JsonProperty(Properties.TICKET)
    public String getTicketURL() {
	return ticketURL;
    }

    @JsonProperty(Properties.DATE)
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE);
    }

    @JsonProperty(Properties.OWNER)
    public String getOwner() {
	return owner;
    }

}
