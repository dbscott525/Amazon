package deprecate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.shared.Util;

public class OnlineRow {

    private String startDateTime;
    private String endDateTime;
    private List<String> oncallMember;

    public OnlineRow(OnCallSchedule onCallSchedule, Map<String, String> nameToUid) {
	startDateTime = getDate(onCallSchedule.getDate(), 0);
	endDateTime = getDate(onCallSchedule.getDate(), 1);

	oncallMember = new ArrayList<Supplier<String>>() {
	    private static final long serialVersionUID = 1L;

	    {
		add(() -> onCallSchedule.getOnCallLead());
		add(() -> onCallSchedule.getOnCall2());
		add(() -> onCallSchedule.getOnCall3());
	    }
	}
		.stream()
		.map(Supplier<String>::get)
		.map(nameToUid::get)
		.collect(Collectors.toList());
    }

    public OnlineRow() {
	// TODO Auto-generated constructor stub
    }

    public String getStartDateTime() {
	return startDateTime;
    }

    public String getEndDateTime() {
	return endDateTime;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public void setStartDateTime(String startDateTime) {
	this.startDateTime = startDateTime;
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

    String getDate(Date date, int dayIncrement) {
	return Util.getDateIncrementString(date, dayIncrement, "M/d/yy") + " 10:00";
    }
}