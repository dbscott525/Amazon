package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateAndSendShadowReminderEmails extends Utility {

    private static final boolean USE_SAVED_SCHEDULE = false;

    private Map<String, OnlineScheduleEvent> primaryScheduleMap;
    private List<OnlineScheduleEvent> primarySchedule;

    public static void main(String[] args) {
	new CreateAndSendShadowReminderEmails().run();
    }

    private void run() {
	primarySchedule = readOnCallSchedule(EngineerType.Primary, USE_SAVED_SCHEDULE)
		.getSchedule();

	primaryScheduleMap = getEventStream(EngineerType.Primary)
		.collect(Collectors.toMap(event -> event.getScheduleKey(), Function.identity()));

	List<EmailRow> emailList = getEventStream(EngineerType.Trainee)
		.map(event -> new EmailRow(event))
		.collect(Collectors.toList());

	EngineerFiles.PRIMARY_SHADOWING_REMINDER_EMAIL_DATA.write(w -> w.CSV(emailList, EmailRow.class));
	EngineerFiles.PRIMARY_SHADOWING_REMINDER_EMAIL.launch();
    }

    private Stream<OnlineScheduleEvent> getEventStream(EngineerType engineerType) {
	return primarySchedule
		.stream()
		.filter(event -> engineerType.engineerIsType(getEngineer(event.getUid())));
    }

    private class EmailRow {

	private String date;
	private String traineeName;
	private String traineeEmail;
	private String email;
	private String primaryName;
	private String shiftTime;

	public EmailRow(OnlineScheduleEvent event) {
	    shiftTime = event.getStartTime();

	    date = Dates.SORTABLE.getFormattedDelta(event.getStartDate(), -1);
	    Engineer trainee = getEngineer(event.getUid());
	    traineeName = trainee.getFirstName();
	    traineeEmail = trainee.getEmail();

	    Engineer primary = getEngineer(primaryScheduleMap.get(event.getScheduleKey()).getUid());
	    email = primary.getEmail();
	    primaryName = primary.getFirstName();

	}

	@SuppressWarnings("unused")
	public String getDate() {
	    return date;
	}

	@SuppressWarnings("unused")
	public String getTraineeName() {
	    return traineeName;
	}

	@SuppressWarnings("unused")
	public String getTraineeEmail() {
	    return traineeEmail;
	}

	@SuppressWarnings("unused")
	public String getEmail() {
	    return email;
	}

	@SuppressWarnings("unused")
	public String getPrimaryName() {
	    return primaryName;
	}

	@SuppressWarnings("unused")
	public String getShiftTime() {
	    return shiftTime;
	}

    }
}
