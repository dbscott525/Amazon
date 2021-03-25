package com.amazon.amsoperations.bean;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.amazon.amsoperations.shared.DateStream;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Properties;
import com.amazon.amsoperations.shared.UnavailabilityDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Unavailability {

    private static final String END_POSTFIX = "End";
    private static final String START_POSTFIX = "Start";
    private static final int NUMBER_OF_DATE_RANGES = 4;
    private static final String END_DATE_3 = "End Date 3";
    private String uid;
    private String start1;
    private String end1;
    private String start2;
    private String end2;
    private String start3;
    private String end3;
    private String start4;
    private String end4;

    @JsonProperty(Properties.ALL_CAPS_UID)
    public String getUid() {
	return uid;
    }

    @JsonProperty(Properties.START_DATE_1)
    public String getStart1() {
	return start1;
    }

    @JsonProperty(Properties.END_DATE_1)
    public String getEnd1() {
	return end1;
    }

    @JsonProperty(Properties.START_DATE_2)
    public String getStart2() {
	return start2;
    }

    @JsonProperty(Properties.END_DATE_2)
    public String getEnd2() {
	return end2;
    }

    @JsonProperty(Properties.START_DATE_3)
    public String getStart3() {
	return start3;
    }

    @JsonProperty(END_DATE_3)
    public String getEnd3() {
	return end3;
    }

    @JsonProperty("Start Date 4")
    public String getStart4() {
	return start4;
    }

    @JsonProperty("End Date 4")
    public String getEnd4() {
	return end4;
    }

    public void setOoo(Engineer engineer) {
	if (engineer == null) {
	    return;
	}
	ArrayList<String> dates = new ArrayList<String>();

	IntStream.rangeClosed(1, NUMBER_OF_DATE_RANGES).forEach(index -> {

	    String startDate = getDate(index, START_POSTFIX);
	    String endDate = getDate(index, END_POSTFIX);

	    if (startDate != null && endDate != null) {
		while (true) {
		    if (startDate.compareTo(endDate) > 0) {
			break;
		    }
		    dates.add(startDate);
		    startDate = Dates.SORTABLE.getFormattedDelta(startDate, 1);
		}
	    }
	});
	engineer.setOoo(dates.toString());
    }

    @JsonIgnore
    private String getDate(int index, String type) {
	try {
	    String stringDate = (String) Unavailability.class.getMethod("get" + type + index).invoke(this);
	    if (stringDate != null && stringDate.length() > 0) {
		return Dates.SORTABLE.getFormattedString(Dates.TT_SEARCH.getDateFromString(stringDate));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public String toString() {
	return "Unavailability [uid=" + uid + ", start1=" + start1 + ", end1=" + end1 + ", start2=" + start2 + ", end2="
		+ end2 + ", start3=" + start3 + ", end3=" + end3 + ", start4=" + start4 + ", end4=" + end4 + "]";
    }

    @JsonIgnore
    public Stream<UnavailabilityDate> getUnvailabilityKeyStream() {
	return IntStream
		.rangeClosed(1, NUMBER_OF_DATE_RANGES)
		.mapToObj(index -> new SingleRange(getDate(index, START_POSTFIX), getDate(index, END_POSTFIX)))
		.filter(SingleRange::haveValidDates)
		.flatMap(SingleRange::getDatStream)
		.map(date -> new UnavailabilityDate(uid, date));
    }

    private class SingleRange {

	private String startDate;
	private String endDate;

	public SingleRange(String startDate, String endDate) {
	    this.startDate = startDate;
	    this.endDate = endDate;
	}

	public boolean haveValidDates() {
	    return Stream
		    .of(startDate, endDate)
		    .allMatch(date -> Objects.nonNull(date));
	}

	public Stream<String> getDatStream() {
	    return DateStream.get(startDate, endDate);
	}

    }

}
