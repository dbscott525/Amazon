package com.amazon.amsoperations.bean;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.amazon.amsoperations.shared.Dates;
import com.google.gson.GsonBuilder;

public class CitScheduleRowDeprecated {

    private String startDateTime;
    private String endDateTime;
    private List<String> oncallMember;

    public CitScheduleRowDeprecated(ScheduleRow scheduleRow) {
	oncallMember = scheduleRow.getEngineers().stream().map(x -> x.getUid()).collect(Collectors.toList());
	extracted(scheduleRow, 0, dateString -> startDateTime = dateString + " 8:00");
	extracted(scheduleRow, 4, dateString -> endDateTime = dateString + " 22:00");
    }

    private void extracted(ScheduleRow scheduleRow, int delta, Consumer<String> dateSetter) {
	dateSetter
		.accept(Dates.ONLINE_SCHEDULE
			.getFormattedString(Dates
				.getDateDelta(Dates.SORTABLE
					.getDateFromString(scheduleRow.getDate()), delta)));
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
