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
import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.DateStream;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Ran;

public class CreateCITWeekData extends Utility implements Command {

    private static final boolean OLD_STUFF = false;

    public static void main(String[] args) throws Exception {
	new CreateCITWeekData().run();
    }

    private List<String> engineersOld;
    private Shift shift;
    private List<Engineer> engineers;

    @Override
    public void run() throws Exception {
	getShiftForThisWeek()
		.ifPresent(this::createFileForShift);
    }

    void createFileForShift(Shift shift) {
	this.shift = shift;
	engineersOld = shift
		.getEngineers()
		.stream()
		.filter(Engineer::isNotServerless)
		.map(Engineer::getFullName)
		.collect(Collectors.toList());
	engineers = shift
		.getEngineers()
		.stream()
		.filter(Engineer::isNotServerless)
		.collect(Collectors.toList());

	createCsvFile();
    }

    private void createCsvFile() {

	if (OLD_STUFF) {
	    Stream<String> dataStream = DateStream.getStream(shift.getDate(), Constants.NUMBER_OF_WEEKDAYS)
		    .map(date -> {
			List<String> randomEngineers = new ArrayList<String>(engineersOld);
			Collections.shuffle(randomEngineers);
			List<String> dates = DateStream.getStream(shift.getDate(), Constants.NUMBER_OF_WEEKDAYS)
				.collect(Collectors.toList());

			Stream<String> foo = Stream
				.of(Arrays.asList(date), engineersOld, randomEngineers, dates)
				.flatMap(List<String>::stream);
			return getCommaSeparatedList(foo);
		    });

	    Stream<String> headerStream = Stream
		    .of(
			    Stream.of("Date"),
			    getHeaderStream("Engineer", engineersOld.size()),
			    getHeaderStream("RandomEngineer", engineersOld.size()),
			    getHeaderStream("Day", Constants.NUMBER_OF_WEEKDAYS))
		    .flatMap(Function.identity());

	    Stream<String> hstream = Stream.of(getCommaSeparatedList(headerStream));
	}

	Context context = new Context();

	Stream<Stream<String>> headerStream = Stream.of(getSegmentStream()
		.flatMap(x -> x.getHeaderStream(context)));

	Stream<Stream<String>> dataStream = DateStream.getStream(shift.getDate(), Constants.NUMBER_OF_WEEKDAYS)
		.map(date -> {
		    context.setDate(date);
		    return getSegmentStream().flatMap(x -> x.getDataStream(context));
		});

	List<String> lines = Stream.concat(headerStream, dataStream)
		.map(x -> x.collect(Collectors.joining(",")))
		.collect(Collectors.toList());

	EngineerFiles.CIT_WEEK_DATA.write(writer -> writer.lines(lines));
    }

    private static Stream<Segment> getSegmentStream() {
	return Stream.of(Segment.values());
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

    private class Context {

	private String date;

	public String getDate() {
	    return date;
	}

	public String getStartDate() {
	    return shift.getDate();
	}

	public void setDate(String date) {
	    this.date = date;
	}

	public int getNumberOfEngineers() {
	    return engineers.size();
	}

	public List<Engineer> getEngineers() {
	    return engineers;
	}

    }

    enum Segment {
	DATE {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return Stream.of("Date");
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return Stream.of(context.getDate());
	    }
	},
	ENGINEERS {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("Engineer", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return getAttributeStream(context, Engineer::getFullName);
	    }

	},
	RANDOM_ENGINEERS {

	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("RandomEngineer", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return context.getEngineers().stream().map(Engineer::getFullName).collect(Ran.toStream());
	    }
	},
	DAY_OF_WEEK {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("Day", Constants.NUMBER_OF_WEEKDAYS);
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return DateStream.getStream(context.getStartDate(), Constants.NUMBER_OF_WEEKDAYS);
	    }
	},
	EMAIL_FILTER {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("Filter", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return getAttributeStream(context, eng -> "Entered by " + eng.getUid());
	    }
	},
	UID {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("UID", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return getAttributeStream(context, Engineer::getUid);
	    }
	},
	NAME {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("Name", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return getAttributeStream(context, Engineer::getFirstName);
	    }
	},
	EMAIL {
	    @Override
	    Stream<String> getHeaderStream(Context context) {
		return getHeaderStream("Email", context.getNumberOfEngineers());
	    }

	    @Override
	    Stream<String> getDataStream(Context context) {
		return getAttributeStream(context, Engineer::getEmail);
	    }
	};

	static Stream<String> getHeaderStream(String prefix, int size) {
	    return IntStream.rangeClosed(1, size)
		    .mapToObj(n -> {
			return prefix + n;
		    });
	}

	abstract Stream<String> getHeaderStream(Context context);

	abstract Stream<String> getDataStream(Context context);

	private static Stream<String> getAttributeStream(Context context, Function<Engineer, String> mapper) {
	    return context.getEngineers().stream().map(mapper);
	}
    }

}
