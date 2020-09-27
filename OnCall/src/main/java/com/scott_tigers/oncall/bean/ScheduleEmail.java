package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.newschedule.Shift;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Util;

public class ScheduleEmail {

    private String date;
    private String teamEmails;
    private String toList;
    private String teamLead;
    private String email;
    private String engineer1;
    private String engineer2;
    private String engineer3;
    private String engineer4;
    private String engineer5;
    private String engineer6;

    public ScheduleEmail(Shift shift) {

	email = Constants.REPLACE_ME_EMAIL;

	List<Engineer> engineers = shift.getEngineers();

	List<Engineer> citEngineers = engineers
		.stream()
		.filter(Engineer::isNotServerless)
		.collect(Collectors.toList());

	teamEmails = Util.getEngineerEmails(citEngineers);
	toList = Util.getEngineerToList(citEngineers);

	date = shift.getDate();

	teamLead = engineers.get(0).getFirstName();

	IntStream.range(0, citEngineers.size()).forEach(index -> {
	    try {
		ScheduleEmail.class
			.getMethod("setEngineer" + (index + 1), String.class)
			.invoke(this, citEngineers.get(index).getFirstName());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	});
    }

    public String getDate() {
	return date;
    }

    public String getTeamEmails() {
	return teamEmails;
    }

    public String getToList() {
	return toList;
    }

    public String getTeamLead() {
	return teamLead;
    }

    public String getEmail() {
	return email;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public void setTeamEmails(String teamEmails) {
	this.teamEmails = teamEmails;
    }

    public void setToList(String toList) {
	this.toList = toList;
    }

    public void setTeamLead(String teamLead) {
	this.teamLead = teamLead;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public void setEngineer1(String engineer1) {
	this.engineer1 = engineer1;
    }

    public String getEngineer2() {
	return engineer2;
    }

    public void setEngineer2(String engineer2) {
	this.engineer2 = engineer2;
    }

    public String getEngineer3() {
	return engineer3;
    }

    public void setEngineer3(String engineer3) {
	this.engineer3 = engineer3;
    }

    public String getEngineer4() {
	return engineer4;
    }

    public void setEngineer4(String engineer4) {
	this.engineer4 = engineer4;
    }

    public String getEngineer5() {
	return engineer5;
    }

    public void setEngineer5(String engineer5) {
	this.engineer5 = engineer5;
    }

    public String getEngineer6() {
	return engineer6;
    }

    public void setEngineer6(String engineer6) {
	this.engineer6 = engineer6;
    }

    public String getEngineer1() {
	return engineer1;
    }

}
