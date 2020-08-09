/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.bean;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.DateStringContainer;
import com.scott_tigers.oncall.schedule.Scheduler;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.ResultCache;

/**
 * (Put description here)
 * 
 * @author bruscob
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Engineer {

    private Double level;
    private String empDate;
    private String endDate;
    private String expertise;
    private String firstName;
    private String lastName;
    private String ooo;
    private String startDate;
    private String timeZone;
    private String trainingDate;
    private String type;
    private String uid;

    private int shiftsCompleted;
    private transient Scheduler scheduler;
    private transient DateStringContainer oooDates;
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
	Boolean dateConflict = dateConflictCache.evaluate(date, () -> {
	    return outOfOffice(date) || beforeStartDate(date) || afterEndDate(date);
	});
	return dateConflict;
    }

    private boolean beforeStartDate(String date) {
//	return false;
	if (!optionalString(startDate).isPresent()) {
	    return false;
	}
	boolean beforeStartDate = Dates.ONLINE_SCHEDULE.convertFormat(startDate, Dates.SORTABLE).compareTo(date) > 0;
	return beforeStartDate;
    }

    private boolean afterEndDate(String date) {
//	return false;
	if (!optionalString(endDate).isPresent()) {
	    return false;
	}
	return Dates.ONLINE_SCHEDULE.convertFormat(endDate, Dates.SORTABLE).compareTo(date) < 0;
    }

//    private boolean afterEndDate(String date) {
//	return dateComparator(endDate, x -> x >= 0);
//    }

    private boolean outOfOffice(String date) {
	return optionalString(ooo)
		.filter(t -> oooConflict(date))
		.isPresent();
    }

    private Optional<String> optionalString(String string) {
	return Optional
		.of(string)
		.filter(s -> !s.isEmpty());
    }

    private boolean oooConflict(String date) {
	return getOOODates()
		.getDates()
		.stream()
		.anyMatch(exclusion -> exclusion.equals(date));

    }

    private DateStringContainer getOOODates() {
	oooDates = Optional
		.ofNullable(oooDates)
		.orElse(new Gson().fromJson("{\"dates\":" + ooo + "}", DateStringContainer.class));

	return oooDates;

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

    public String getStartDate() {
	return startDate;
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    public String getExpertise() {
	return expertise;
    }

    public void setExpertise(String expertise) {
	this.expertise = expertise;
    }

    public String getFullNameWithUid() {
	return getFullName() + " (" + uid + ")";
    }

    public String getEmpDate() {
	return empDate;
    }

    public void setEmpDate(String empDate) {
	this.empDate = empDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public void candidateStartDate(String candidateStartDate) {
	if (uid.equals("vibagade")) {
	    System.out.println("uid=" + (uid));
	    System.out.println("startDate=" + (startDate));
	    System.out.println("candidateStartDate=" + (candidateStartDate));
	}
	startDate = optionalString(startDate)
		.filter(currentStartDate -> Dates.ONLINE_SCHEDULE
			.convertFormat(currentStartDate, Dates.SORTABLE).compareTo(candidateStartDate) > 0)
		.orElse(Dates.SORTABLE.convertFormat(candidateStartDate, Dates.ONLINE_SCHEDULE));
	if (uid.equals("vibagade")) {
	    System.out.println("startDate=" + (startDate));
	}
    }
}
