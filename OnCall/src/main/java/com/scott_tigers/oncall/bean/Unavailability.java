package com.scott_tigers.oncall.bean;

import java.util.ArrayList;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.shared.Dates;

public class Unavailability {

    private String uid;
    private String start1;
    private String end1;
    private String start2;
    private String end2;
    private String start3;
    private String end3;
    private String start4;
    private String end4;

    public String getUid() {
	return uid;
    }

    public String getStart1() {
	return start1;
    }

    public String getEnd1() {
	return end1;
    }

    public String getStart2() {
	return start2;
    }

    public String getEnd2() {
	return end2;
    }

    public String getStart3() {
	return start3;
    }

    public String getEnd3() {
	return end3;
    }

    public String getStart4() {
	return start4;
    }

    public String getEnd4() {
	return end4;
    }

    public void setOoo(Engineer engineer) {
	if (engineer == null) {
	    return;
	}
	ArrayList<String> dates = new ArrayList<String>();

	IntStream.range(1, 5).forEach(index -> {

	    String startDate = extracted(index, "Start");
	    String endDate = extracted(index, "End");

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

    private String extracted(int index, String type) {
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

}
