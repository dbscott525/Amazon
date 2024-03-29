package com.amazon.amsoperations.bean;

import com.amazon.amsoperations.shared.Properties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EngineerMetric {

    private transient int weeks = 0;
    private transient int tickets = 0;
    private String name;
    private Double ticketsPerWeek;
    private int weeksToSkip;

    public EngineerMetric(Engineer eng) {
	name = eng.getFullName();
	weeksToSkip = eng.getDynamicActionWeeks();
    }

    public EngineerMetric(String name, double ticketsPerWeek) {
	this.name = name;
	this.ticketsPerWeek = ticketsPerWeek;
    }

    public void addWeek() {
	weeks += weeksToSkip-- > 0 ? 0 : 1;
//	weeks++;
	updateTicketsPerWeek();
    }

    private void updateTicketsPerWeek() {
	ticketsPerWeek = weeks == 0 ? 0 : (double) tickets / weeks;
//	ticketsPerWeek = (double) tickets / weeks;
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

    @JsonProperty(Properties.TICKETS_PER_MONTH)
    public double getTicketsPerWeek() {
	return ticketsPerWeek;
    }

    public void setTicketsPerWeek(double ticketsPerWeek) {
	this.ticketsPerWeek = ticketsPerWeek;
    }

}
