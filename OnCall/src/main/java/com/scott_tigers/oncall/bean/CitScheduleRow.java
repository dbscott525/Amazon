package com.scott_tigers.oncall.bean;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Dates;

public class CitScheduleRow {

    private String startDateTime;
    private String endDateTime;
    private List<String> oncallMember;

    public CitScheduleRow(Shift shift) {
	oncallMember = shift
		.getEngineers()
		.stream()
		.map(Engineer::getUid)
		.collect(Collectors.toList());

	setDateTime(shift, -1, dateString -> startDateTime = dateString + " 8:00");
	setDateTime(shift, 4, dateString -> endDateTime = dateString + " 22:00");
    }

    public CitScheduleRow(String uid, String date) {
	oncallMember = List.of(uid);
	String nextDay = Dates.ONLINE_SCHEDULE.getFormattedDelta(date, 1);
	startDateTime = date + " 10:00";
	endDateTime = nextDay + " 10:00";
    }

    private void setDateTime(Shift shift, int delta, Consumer<String> dateSetter) {
	dateSetter
		.accept(Dates.ONLINE_SCHEDULE
			.getFormattedString(Dates
				.getDateDelta(Dates.SORTABLE
					.getDateFromString(shift.getDate()), delta)));
    }

    public String getStartDateTime() {
	return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
	this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
	return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
	this.endDateTime = endDateTime;
    }

    public List<String> getOncallMember() {
	return oncallMember;
    }

    public void setOncallMember(List<String> oncallMember) {
	this.oncallMember = oncallMember;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
