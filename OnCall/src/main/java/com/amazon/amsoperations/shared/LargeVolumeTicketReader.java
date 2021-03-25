package com.amazon.amsoperations.shared;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.utility.Utility;

public class LargeVolumeTicketReader extends Utility {

    public static Stream<TT> getStream(Consumer<LargeVolumeTicketReader> parameterSetter) {
	LargeVolumeTicketReader reader = new LargeVolumeTicketReader();
	parameterSetter.accept(reader);
	return reader.getInternalStream();
    }

    private int daysPerSearch = 100;
    private String startDate;
    private String urlTemplate;

    private Stream<TT> getInternalStream() {
	String endDate = Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedString(), 1);
	return DateStream.get(startDate, endDate, daysPerSearch)
		.peek(date -> System.out.println("date=" + (date)))
		.map(searchStart -> Dates.SORTABLE.convertFormat(searchStart, Dates.TT_SEARCH))
		.map(this::getUrl)
		.flatMap(this::getTicketStream);
    }

    private Stream<TT> getTicketStream(String url) {
	try {
	    return getTicketStreamFromUrl(url);
	} catch (Exception e) {
	    Stream<TT> stream = Stream.empty();
	    return stream;
	}
    }

    private String getUrl(String searchStart) {
	String searchEnd = Dates.TT_SEARCH.getFormattedDelta(searchStart, daysPerSearch);
	return urlTemplate.replaceAll("(.*?)START(.*?)END(.*)",
		"$1" + searchStart + "$2" + searchEnd + "$3");
    }

    public LargeVolumeTicketReader urlTemplate(String urlTemplate) {
	this.urlTemplate = urlTemplate;
	return this;
    }

    public LargeVolumeTicketReader daysPerSearch(int daysPerSearch) {
	this.daysPerSearch = daysPerSearch;
	return this;
    }

    public LargeVolumeTicketReader startDate(String startDate) {
	this.startDate = startDate;
	return this;
    }
}
