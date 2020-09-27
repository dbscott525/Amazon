package com.scott_tigers.oncall.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Oncall;

public class CreateOncallSchedule extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateOncallSchedule().run();
    }

    public void run() throws Exception {

	EngineerFiles.ON_CALL_SCHEDULE.write(w -> w.CSV(
		Stream
			.concat(getOnCallStream(), getTraineeStream())
			.distinct()
			.collect(Collectors.toList()),
		OnCallScheduleRow.class));
    }

    private Stream<OnCallScheduleRow> getTraineeStream() {
	return EngineerFiles.TRAINING_DAILY_SCHEDULE
		.readLines()
		.stream()
		.flatMap(line -> {
		    ArrayList<OnCallScheduleRow> lines = new ArrayList<>();

		    String date = line.replaceAll("(.*?),.*", "$1");

		    Pattern p = Pattern.compile("\\(.*?\\)");
		    Matcher m = p.matcher(line);

		    while (m.find()) {
			String uid = m.group().replaceAll("\\((.*?)\\)", "$1");
			lines.add(new OnCallScheduleRow(date, uid));
		    }

		    return lines.stream();
		});
    }

    private Stream<OnCallScheduleRow> getOnCallStream() {
	return Stream
		.of(Oncall.values())
		.flatMap(Oncall::getOnCallScheduleStream);
    }
}