package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.Properties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketMetric implements Comparable<TicketMetric> {
    Integer createDateCount = 0;
    Integer resolvedDateCount = 0;
    Integer open = 0;
    String date;

    public TicketMetric(String date) {
	this.date = date;
    }

    public TicketMetric() {
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

    @JsonProperty(Properties.CREATED)
    public Integer getCreateDateCount() {
	return createDateCount;
    }

    public void setCreateDateCount(Integer createDateCount) {
	this.createDateCount = createDateCount;
    }

    @JsonProperty(Properties.RESOLVED)
    public Integer getResolvedDateCount() {
	return resolvedDateCount;
    }

    public void setResolvedDateCount(Integer resolvedDateCount) {
	this.resolvedDateCount = resolvedDateCount;
    }

    @JsonProperty(Properties.DATE)
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @JsonProperty(Properties.OPEN)
    public Integer getOpen() {
	return open;
    }

    public void setOpen(Integer opened) {
	this.open = opened;
    }

    public void setResolved(Integer resolved) {
	this.resolvedDateCount = resolved;
    }

}
