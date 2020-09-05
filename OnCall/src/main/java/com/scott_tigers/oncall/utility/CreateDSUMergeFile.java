package com.scott_tigers.oncall.utility;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.DateStream;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateDSUMergeFile extends Utility {

    private static final int NUMBER_OF_ENGINEERS = 6;
    private static final int DAYS_PER_WEEK = 5;

    public static void main(String[] args) {
	new CreateDSUMergeFile().run();
    }

    private void run() {
	getScheduleForThisWeek().ifPresent(shift -> {

	    List<String> engineerNames = shift
		    .getEngineers()
		    .stream()
		    .filter(Engineer::isNotServerless)
		    .map(Engineer::getFullName)
		    .collect(Collectors.toList());

	    Stream<String> engineerHeaderStream = IntStream
		    .rangeClosed(1, NUMBER_OF_ENGINEERS)
		    .mapToObj(n -> "Engineer" + n);
	    Stream<String> dateHader = Stream.of("Date");

	    Stream<String> headerStream = Stream.of(Stream.concat(
		    dateHader,
		    engineerHeaderStream)
		    .collect(Collectors.joining(",")));

	    Stream<String> rowsStream = DateStream.getStream(shift.getDate(), DAYS_PER_WEEK).map(date -> {

		Collections.shuffle(engineerNames);

		Stream<String> dateStream = Stream.of("\"" + Dates.SORTABLE.convertFormat(date, Dates.NICE) + "\"");
		Stream<String> engineersStream = engineerNames.stream();
		Stream<String> padStream = IntStream
			.range(0, NUMBER_OF_ENGINEERS - engineerNames.size())
			.mapToObj(n -> "");

		return Stream
			.of(dateStream, engineersStream, padStream)
			.flatMap(Function.identity())
			.collect(Collectors.joining(","));
	    });

	    writeLines(EngineerFiles.DSU_DATA, Stream
		    .concat(headerStream, rowsStream)
		    .collect(Collectors.toList()));
	});

    }

}
