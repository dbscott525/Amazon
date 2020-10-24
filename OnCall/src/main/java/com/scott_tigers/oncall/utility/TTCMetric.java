package com.scott_tigers.oncall.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Properties;

public class TTCMetric implements Comparable<TTCMetric> {
    transient int tickets = 0;
    transient int totalDays = 0;
    private double averageDays;
    private String firstDayOfMonth;

    public void add(String firstDayOfMonth, long days) {
	this.firstDayOfMonth = firstDayOfMonth;
	tickets++;
	totalDays += days;
	averageDays = (double) totalDays / tickets;
    }

    @Override
    public int compareTo(TTCMetric o) {
	return firstDayOfMonth.compareTo(o.firstDayOfMonth);
    }

    @JsonProperty(Properties.DAYS)
    public double getAverageDays() {
	return averageDays;
    }

    public void setAverageDays(double averageDays) {
	this.averageDays = averageDays;
    }

    @JsonProperty(Properties.DATE)
    public String getFirstDayOfMonth() {
	return firstDayOfMonth;
    }

    public void setFirstDayOfMonth(String firstDayOfMonth) {
	this.firstDayOfMonth = firstDayOfMonth;
    }

}
