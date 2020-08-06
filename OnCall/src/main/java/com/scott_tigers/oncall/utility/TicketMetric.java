package com.scott_tigers.oncall.utility;

import java.lang.reflect.Method;

public class TicketMetric implements Comparable<TicketMetric> {
    Integer createDateCount = 0;
    Integer resolvedDateCount = 0;
    String date;

    public TicketMetric(String date) {
	this.date = date;
    }

    public void addDataPoint(String dateType) {
	try {
	    System.out.println("dateType");
	    String getMethodName = "get" + dateType + "Count";
	    System.out.println("getMethodName=" + (getMethodName));
	    Method m1 = TicketMetric.class.getMethod(getMethodName);
	    int count = (int) m1.invoke(this);
	    count++;
	    String setMethodName = "set" + dateType + "Count";
	    System.out.println("setMethodName=" + (setMethodName));
	    Method m2 = TicketMetric.class.getMethod(setMethodName, Integer.class);
	    m2.invoke(this, count);

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
