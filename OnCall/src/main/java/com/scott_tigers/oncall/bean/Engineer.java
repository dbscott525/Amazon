/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.bean;

import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.DateStringContainer;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Expertise;
import com.scott_tigers.oncall.shared.ResultCache;

/**
 * (Put description here)
 * 
 * @author bruscob
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Engineer {

    private Double level;
    private Double levelAdjust;
    private String empDate;
    private String endDate;
    private String expertise;
    private String firstName = "";
    private String lastName = "";
    private String ooo;
    private String oncallStartDate;
    private String startDate;
    private String timeZone;
    private String trainingDate;
    private String type;
    private String uid;

    private transient int shiftsCompleted;
    private transient DateStringContainer oooDates;
    private transient ResultCache<String, Boolean> dateConflictCache = new ResultCache<String, Boolean>();

    public Engineer(OnlineScheduleEvent onCallScheduleEvent) {
	uid = onCallScheduleEvent.getUid();
	type = onCallScheduleEvent.getType();
    }

    public Engineer() {
    }

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

    public Double getLevelAdjust() {
	return levelAdjust;
    }

    public void setLevelAdjust(Double levelAdjust) {
	this.levelAdjust = levelAdjust;
    }

    @JsonIgnore
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
	if (!optionalString(startDate).isPresent()) {
	    return false;
	}
	boolean beforeStartDate = Dates.ONLINE_SCHEDULE.convertFormat(startDate, Dates.SORTABLE).compareTo(date) > 0;
	return beforeStartDate;
    }

    public boolean afterEndDate(String date) {
	if (!optionalString(endDate).isPresent()) {
	    return false;
	}
	return Dates.ONLINE_SCHEDULE.convertFormat(endDate, Dates.SORTABLE).compareTo(date) < 0;
    }

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

    @JsonIgnore
    public String getFullName() {
	return firstName + " " + lastName;
    }

    @JsonIgnore
    public String getFullNameWithExertise() {
	return getFullName() + Expertise.get(expertise).getNotation();
    }

    @JsonIgnore
    public String getEmail() {
	return uid + Constants.AMAZON_EMAIL_POSTFIX;
    }

    @JsonIgnore
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

    @JsonIgnore
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

    public String getOncallStartDate() {
	return oncallStartDate;
    }

    public void setOncallStartDate(String oncallStartDate) {
	this.oncallStartDate = oncallStartDate.isEmpty()
		? null
		: Dates.ONLINE_SCHEDULE.convertFormat(oncallStartDate, Dates.SORTABLE);

    }

    public void candidateStartDate(String candidateStartDate) {
	startDate = optionalString(startDate)
		.filter(currentStartDate -> Dates.ONLINE_SCHEDULE
			.convertFormat(currentStartDate, Dates.SORTABLE).compareTo(candidateStartDate) > 0)
		.orElse(Dates.SORTABLE.convertFormat(candidateStartDate, Dates.ONLINE_SCHEDULE));
    }

    @JsonIgnore
    public boolean isValidTrainingDate() {
	return Optional
		.ofNullable(trainingDate)
		.filter(Predicate.not(String::isEmpty))
		.filter(t -> isWithinAMonth(t))
		.isPresent();
    }

    private boolean isWithinAMonth(String date) {
	String trainingDateString = Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE);
	String fourWeeksAgo = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), -4 * 7);
	return trainingDateString.compareTo(fourWeeksAgo) >= 0;
    }

    @SuppressWarnings("unused")
    private boolean isAfterToday(String date) {
	return new Date().compareTo(Dates.ONLINE_SCHEDULE.getDateFromString(date)) < 0;
    }

    public int getRequiredOrder() {
	return Expertise.get(expertise).getRequiredOrder();
    }

    @JsonIgnore
    public boolean isBeforeEndDate() {
	return !Optional.ofNullable(endDate)
		.filter(endDate -> Dates.ONLINE_SCHEDULE.getDateFromString(endDate).compareTo(new Date()) < 0)
		.isPresent();
    }

    @JsonIgnore
    public boolean isAfterStartDate() {
	return !Optional.ofNullable(startDate)
		.filter(endDate -> Dates.ONLINE_SCHEDULE.getDateFromString(startDate).compareTo(new Date()) >= 0)
		.isPresent();
    }

    @JsonIgnore
    public boolean isNotServerless() {
	return Expertise.get(expertise) != Expertise.Serverless;
    }

    public boolean isType(EngineerType engineerType) {
	return type.compareTo(engineerType.toString()) == 0;
    }

    @JsonIgnore
    public boolean isCurrent() {
	return isBeforeEndDate() && isAfterStartDate();
    }
}
