package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Properties;

public class EngineerMetric {

    private transient int weeks = 0;
    private transient int tickets = 0;
    private String name;
    private Double ticketsPerWeek;

    public EngineerMetric(Engineer eng) {
	name = eng.getFullName();
    }

    public EngineerMetric(String name, double ticketsPerWeek) {
	this.name = name;
	this.ticketsPerWeek = ticketsPerWeek;
    }

    public void addWeek() {
	weeks++;
	updateTicketsPerWeek();
    }

    private void updateTicketsPerWeek() {
	ticketsPerWeek = (double) tickets / weeks;

    }

    @JsonIgnore
    public int getWeeks() {
	return weeks;
    }

    public void setWeeks(int weeks) {
	this.weeks = weeks;
    }

    @JsonIgnore
    public int getTickets() {
	return tickets;
    }

    public void setTickets(int tickets) {
	this.tickets = tickets;
    }

    public void addTicket() {
	tickets++;
	updateTicketsPerWeek();
    }

    @JsonProperty(Properties.NAME)
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @JsonProperty(Properties.TICKETS_PER_WEEK)
    public double getTicketsPerWeek() {
	return ticketsPerWeek;
    }

    public void setTicketsPerWeek(double ticketsPerWeek) {
	this.ticketsPerWeek = ticketsPerWeek;
    }

}
