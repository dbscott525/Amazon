package com.scott_tigers.oncall.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.newschedule.Shift;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.DateStream;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCITWeekData extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateCITWeekData().run();
    }

    private List<String> engineers;
    private Shift shift;

    @Override
    public void run() throws Exception {
	getShiftForThisWeek()
		.ifPresent(this::createFileForShift);
    }

    void createFileForShift(Shift shift) {
	this.shift = shift;
	engineers = shift
		.getEngineers()
		.stream()
		.filter(Engineer::isNotServerless)
		.map(Engineer::getFullName)
		.collect(Collectors.toList());

	createCsvFile();
    }

    private void createCsvFile() {
	Stream<String> dataStream = DateStream.getStream(shift.getDate(), Constants.NUMBER_OF_WEEKDAYS).map(date -> {
	    List<String> randomEngineers = new ArrayList<String>(engineers);
	    Collections.shuffle(randomEngineers);
	    List<String> dates = DateStream.getStream(shift.getDate(), Constants.NUMBER_OF_WEEKDAYS)
		    .collect(Collectors.toList());

	    Stream<String> foo = Stream
		    .of(Arrays.asList(date), engineers, randomEngineers, dates)
		    .flatMap(List<String>::stream);
	    return getCommaSeparatedList(foo);
	});

	Stream<String> headerStream = Stream
		.of(
			Stream.of("Date"),
			getHeaderStream("Engineer", engineers.size()),
			getHeaderStream("RandomEngineer", engineers.size()),
			getHeaderStream("Day", Constants.NUMBER_OF_WEEKDAYS))
		.flatMap(Function.identity());

	Stream<String> hstream = Stream.of(getCommaSeparatedList(headerStream));

	List<String> lines = Stream.concat(hstream, dataStream).collect(Collectors.toList());

	EngineerFiles.CIT_WEEK_DATA.write(writer -> writer.lines(lines).noOpen());
    }

    private String getCommaSeparatedList(Stream<String> contentStream) {
	return contentStream.collect(Collectors.joining(","));
    }

    private Stream<String> getHeaderStream(String prefix, int size) {
	return IntStream.rangeClosed(1, size)
		.mapToObj(n -> {
		    return prefix + n;
		});
    }

}
