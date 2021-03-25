package com.amazon.amsoperations.utility;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.EmailsByDate;
import com.amazon.amsoperations.shared.EngineerFiles;

public class CreateTraineeEmails extends Utility {

    public static void main(String[] args) {
	new CreateTraineeEmails().run();

    }

    private void run() {
	List<EmailsByDate> traineeEmailList = getTraineesByDate()
		.entrySet()
		.stream()
		.map(EmailsByDate::fromTrainee)
		.sorted(Comparator.comparing(EmailsByDate::getDate))
		.collect(Collectors.toList());

	EngineerFiles.TRAINEE_EMAILS.write(w -> w.CSV(traineeEmailList, EmailsByDate.class));
	EngineerFiles.TRAINEE_INTRODUCTION.launch();
	EngineerFiles.TRAINEE_TRAINING_COMPLETE.launch();
    }

}
