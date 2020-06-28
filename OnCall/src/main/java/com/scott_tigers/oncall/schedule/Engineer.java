/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
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

    @Override
    public String toString() {
	return "Engineer [name=" + firstName + ", level=" + level + ", exclusionDates=" + ooo + "]";
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

}
