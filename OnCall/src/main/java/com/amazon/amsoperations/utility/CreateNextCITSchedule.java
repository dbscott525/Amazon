package com.amazon.amsoperations.utility;

import java.io.IOException;

import com.amazon.amsoperations.schedule.ScheduleCreator;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("unchecked")
public class CreateNextCITSchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateNextCITSchedule().run();
    }

    private void run() throws IOException {

	String startDate = Dates.ONLINE_SCHEDULE
		.convertFormat(EngineerFiles.CIT_SCHEDULE_CHANGE_NOTIFICATION_EMAIL_DATA
			.readCSVToPojo(Notification.class)
			.get(0)
			.getDate(), Dates.SORTABLE);
	System.out.println("startDate=" + (startDate));

	new ScheduleCreator()
		.startDate(startDate)
//		.endDate("2020-12-31")
//		.endAfterMonths(6)
		.endAfterWeeksFromNow(12)
		.shifts(12)
		.shiftSize(9)
		.maximumShiftFrequency(5)
		.daysBetweenShifts(7)
//		.iterations(10000)
		.timeLimit(20)
		.create();

	runCommands(
		CreateCITOnlineSchedule.class,
		CreateCSVSchedule.class);

	launchUrl(URL.CIT_SCHEDULE);
	EngineerFiles.CIT_SCHEDULE_CHANGE_NOTIFICATION_EMAIL.launch();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Notification {
	private String email;
	private String date;

	public String getEmail() {
	    return email;
	}

	public void setEmail(String email) {
	    this.email = email;
	}

	public String getDate() {
	    return date;
	}

	public void setDate(String date) {
	    this.date = date;
	}

    }
}
