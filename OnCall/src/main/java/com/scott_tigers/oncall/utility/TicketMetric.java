package com.scott_tigers.oncall.utility;

public class TicketMetric implements Comparable<TicketMetric> {
    Integer createDateCount = 0;
    Integer resolvedDateCount = 0;
    String date;

    public TicketMetric(String date) {
	this.date = date;
    }

    public void addDataPoint(String dateType) {
	try {
	    int count = (int) TicketMetric.class
		    .getMethod("get" + dateType + "Count")
		    .invoke(this);

	    count++;

	    TicketMetric.class
		    .getMethod("set" + dateType + "Count", Integer.class)
		    .invoke(this, count);

	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

    }

    @Override
    public int compareTo(TicketMetric o) {
	return date.compareTo(o.date);
    }

    public Integer getCreateDateCount() {
	return createDateCount;
    }

    public void setCreateDateCount(Integer createDateCount) {
	this.createDateCount = createDateCount;
    }

    public Integer getResolvedDateCount() {
	return resolvedDateCount;
    }

    public void setResolvedDateCount(Integer resolvedDateCount) {
	this.resolvedDateCount = resolvedDateCount;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

}
