/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall;

import com.google.gson.Gson;

/**
 * (Put description here)
 * 
 * @author bruscob
 */

public class Engineer {

    private String name;
    private double level;
    private String exclusionDates;
    private Scheduler scheduler;
    private DateStringContainer dates;
    private ResultCache<String, Boolean> dateConflictCache = new ResultCache<String, Boolean>();
    private ResultCache<Integer, Boolean> percentileCache = new ResultCache<Integer, Boolean>();

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public double getLevel() {
	return level;
    }

    public void setLevel(double level) {
	this.level = level;
    }

    public String getExclusionDates() {
	return exclusionDates;
    }

    public void setExclusionDates(String exclusionDates) {
	this.exclusionDates = exclusionDates;
    }

    @Override
    public String toString() {
	return "Engineer [name=" + name + ", level=" + level + ", exclusionDates=" + exclusionDates + "]";
    }

    public boolean hasDateConflict(String date) {
	return dateConflictCache.evaluate(date, () -> {
	    if (exclusionDates == null || exclusionDates.length() == 0) {
		return false;
	    }

	    if (dates == null) {
		dates = new Gson().fromJson("{\"dates\":" + exclusionDates + "}", DateStringContainer.class);
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

//	return scheduler.isGreaterThanPercnetile(percentile, level);
    }

}
