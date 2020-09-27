package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WIP {

    private String ticketURL;

    @JsonProperty(Properties.TICKET)
    public String getTicketURL() {
	return ticketURL;
    }

    public void setTicketURL(String ticketURL) {
	this.ticketURL = ticketURL;
    }

}
