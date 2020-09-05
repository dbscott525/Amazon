package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Dates;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LTTRPlan {
    private String ticketsPerWeek;
    private String area;
    private String release;
    private String date;

    @JsonProperty("Tickets Per Week")
    public String getTicketsPerWeek() {
	return ticketsPerWeek;
    }

    public void setTicketsPerWeek(String ticketsPerWeek) {
	this.ticketsPerWeek = ticketsPerWeek;
    }

    @JsonProperty("Area")
    public String getArea() {
	return area;
    }

    public void setArea(String area) {
	this.area = area;
    }

    @JsonProperty("Release")
    public String getRelease() {
	return release;
    }

    public void setRelease(String release) {
	this.release = release;
    }

    @JsonProperty("Date")
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public Double getDoubleTicketsPerWeek() {
	return Double.valueOf(ticketsPerWeek);
    }

    public String getMonth() {
	return Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.YEAR_MONTH);
    }

}
