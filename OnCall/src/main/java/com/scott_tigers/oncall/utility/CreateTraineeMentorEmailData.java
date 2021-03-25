package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateTraineeMentorEmailData extends Utility {

    public static void main(String[] args) {
	new CreateTraineeMentorEmailData().run();
    }

    private void run() {

	List<MentorEmail> emailData = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(eng -> EngineerType.Trainee.engineerIsType(eng))
		.map(eng -> new MentorEmail(eng))
		.collect(Collectors.toList());
	EngineerFiles.MENTOR_EMAIL_DATA.write(w -> w.CSV(emailData, MentorEmail.class));

    }

    private class MentorEmail {
	private String email;
	private String name;
	private String mentorEmail;
	private String mentor;

	public MentorEmail(Engineer eng) {
	    email = eng.getEmail();
	    name = eng.getFirstName();
	    Engineer mentorEng = getEngineer(eng.getMentor());
	    mentorEmail = mentorEng.getEmail();
	    mentor = mentorEng.getFullName();
	}

	public String getEmail() {
	    return email;
	}

	public String getName() {
	    return name;
	}

	public String getMentorEmail() {
	    return mentorEmail;
	}

	public String getMentor() {
	    return mentor;
	}

    }
}