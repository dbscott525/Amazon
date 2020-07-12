/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.DateStringContainer;
import com.scott_tigers.oncall.schedule.Scheduler;
import com.scott_tigers.oncall.shared.ResultCache;

/**
 * (Put description here)
 * 
 * @author bruscob
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Engineer {

    private String firstName;
    private String lastName;
    private String type;
    private String uid;
    private Double level;
    private String ooo;
    private String trainingDate;
    private String timeZone;

    private int shiftsCompleted;
    private transient Scheduler scheduler;
    private transient DateStringContainer dates;
    private transient ResultCache<String, Boolean> dateConflictCache = new ResultCache<String, Boolean>();
    private transient ResultCache<Integer, Boolean> percentileCache = new ResultCache<Integer, Boolean>();

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getUid() {
	return uid;
    }

    public void setUid(String uid) {
	this.uid = uid;
    }

    public Double getLevel() {
	return level;
    }

    public void setLevel(Double level) {
	this.level = level;
    }

    public String getOoo() {
	return ooo;
    }

    public void setOoo(String ooo) {
	this.ooo = ooo;
    }

    public boolean hasDateConflict(String date) {
	return dateConflictCache.evaluate(date, () -> {
	    if (ooo == null || ooo.length() == 0) {
		return false;
	    }

	    if (dates == null) {
		dates = new Gson().fromJson("{\"dates\":" + ooo + "}", DateStringContainer.class);
	    }

	    boolean conflict = dates
		    .getDates().stream()
		    .anyMatch(exclusion -> exclusion.equals(date));
	    return conflict;
	});
    }

    public void setScheduler(Scheduler scheduler) {
	this.scheduler = scheduler;
    }

    public boolean isGreaterThanPercentile(int percentile) {
	return percentileCache
		.evaluate(percentile,
			() -> scheduler.isGreaterThanPercnetile(percentile, level));
    }

    public String getFullName() {
	return firstName + " " + lastName;
    }

    public String getEmail() {
	return uid + "@amazon.com";
    }

    public int getShiftsCompleted() {
	return shiftsCompleted;
    }

    public void setShiftsCompleted(int shiftsCompleted) {
	this.shiftsCompleted = shiftsCompleted;
    }

    public void incrementShiftsCompleted() {
	shiftsCompleted++;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((uid == null) ? 0 : uid.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Engineer other = (Engineer) obj;
	if (uid == null) {
	    if (other.uid != null)
		return false;
	} else if (!uid.equals(other.uid))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public String getTrainingDate() {
	return trainingDate;
    }

    public String getTimeZone() {
	return timeZone;
    }

}
