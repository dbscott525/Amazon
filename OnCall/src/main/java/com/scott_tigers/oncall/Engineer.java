/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall;

import java.util.Arrays;

/**
 * (Put description here)
 * 
 * @author bruscob
 */

public class Engineer {

    private String name;
    private double level;
    private String exclusionDates;

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

    public boolean hasDayConflig(String date) {
	if (exclusionDates == null) {
	    return false;
	}

	return Arrays
		.stream(exclusionDates.split(","))
		.anyMatch(exclusion -> exclusion.equals(date));
    }

}
