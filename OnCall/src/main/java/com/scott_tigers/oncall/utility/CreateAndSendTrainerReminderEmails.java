package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class CreateAndSendTrainerReminderEmails extends Utility {

    public static void main(String[] args) {
	new CreateAndSendTrainerReminderEmails().run();
    }

    private void run() {
	Map<String, Engineer> engineerMap = EngineerFiles.MASTER_LIST.readCSV().stream()
		.collect(Collectors.toMap(x -> x.getFullName(), x -> x));
	AtomicReference<String> lastDate = new AtomicReference<String>();
	List<TrainingSchedule> emails = readFromUrl(URL.PRIMARY_TRAINING_SCHEDULE, TrainingSchedule.class)
		.peek(x -> x.addData(engineerMap, lastDate))
		.collect(Collectors.toList());

	EngineerFiles.PRIMARY_TRAINER_REMINDER_DATA.write(w -> w.CSV(emails, TrainingSchedule.class));
	EngineerFiles.PRIMARY_TRAINER_REMINDER_EMAIL.launch();

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TrainingSchedule {
	private String date;
	private String topic;
	private String trainer;
	private String sendDate;
	private String firstName;
	private String email;
	private String time;

	@SuppressWarnings("unused")
	public TrainingSchedule() {
	}

	@JsonProperty(Properties.DATE)
	public String getDate() {
	    return date;
	}

	public void addData(Map<String, Engineer> engineerMap, AtomicReference<String> lastDate) {
	    System.out.println("trainer=" + (trainer));
	    Engineer eng = engineerMap.get(trainer);
	    firstName = eng.getFirstName();
	    email = eng.getEmail();
	    date = date.length() == 0 ? lastDate.get() : date;
	    lastDate.set(date);
	}

	@JsonProperty("Topic")
	public String getTopic() {
	    return topic;
	}

	@JsonProperty("Trainer")
	public String getTrainer() {
	    return trainer;
	}

	@SuppressWarnings("unused")
	public String getSendDate() {
	    return Dates.ONLINE_SCHEDULE.getFormattedDelta(date, -7);
	}

	@SuppressWarnings("unused")
	public String getEmail() {
	    return email;
	}

	@JsonProperty("Time")
	public String getTime() {
	    return time;
	}

	@SuppressWarnings("unused")
	public String getFirstName() {
	    return firstName;
	}

    }
}
