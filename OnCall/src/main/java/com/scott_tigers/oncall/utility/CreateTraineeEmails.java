package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.EmailsByDate;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateTraineeEmails extends Utility {

    public static void main(String[] args) {
	new CreateTraineeEmails().run();

    }

    private void run() {
	EngineerFiles.TRAINEE_EMAILS
		.writeCSV(getTraineesByDate()
			.entrySet()
			.stream()
			.map(EmailsByDate::fromTrainee)
			.collect(Collectors.toList()), EmailsByDate.class);

	successfulFileCreation(EngineerFiles.TRAINEE_EMAILS);
    }

}
