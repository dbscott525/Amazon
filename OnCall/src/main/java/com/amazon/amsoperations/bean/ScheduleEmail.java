package com.amazon.amsoperations.bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.amazon.amsoperations.schedule.Shift;
import com.amazon.amsoperations.shared.Constants;
import com.amazon.amsoperations.shared.Util;

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
    private String engineer7;
    private String engineer8;
    private String engineer9;
    private String engineer10;
    private String engineer11;
    private String engineer12;
    private String engineer13;
    private String engineer14;
    private String engineer15;

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
	    Engineer engineer = citEngineers.get(index);
	    try {
		ScheduleEmail.class
			.getMethod("setEngineer" + (index + 1), String.class)
			.invoke(this, engineer.getFirstName());
	    } catch (Exception e) {
		System.out.println("engineer=" + (engineer));
		e.printStackTrace();
		System.exit(0);
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

    public String getEngineer7() {
	return engineer7;
    }

    public void setEngineer7(String engineer7) {
	this.engineer7 = engineer7;
    }

    public String getEngineer8() {
	return engineer8;
    }

    public void setEngineer8(String engineer8) {
	this.engineer8 = engineer8;
    }

    public String getEngineer9() {
	return engineer9;
    }

    public void setEngineer9(String engineer9) {
	this.engineer9 = engineer9;
    }

    public String getEngineer10() {
	return engineer10;
    }

    public void setEngineer10(String engineer10) {
	this.engineer10 = engineer10;
    }

    public String getEngineer11() {
	return engineer11;
    }

    public void setEngineer11(String engineer11) {
	this.engineer11 = engineer11;
    }

    public String getEngineer12() {
	return engineer12;
    }

    public void setEngineer12(String engineer12) {
	this.engineer12 = engineer12;
    }

    public String getEngineer13() {
	return engineer13;
    }

    public void setEngineer13(String engineer13) {
	this.engineer13 = engineer13;
    }

    public String getEngineer14() {
	return engineer14;
    }

    public void setEngineer14(String engineer14) {
	this.engineer14 = engineer14;
    }

    public String getEngineer15() {
	return engineer15;
    }

    public void setEngineer15(String engineer15) {
	this.engineer15 = engineer15;
    }

}
