package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.function.Predicate;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreatePrimaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreatePrimaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	super.run();
    }

    @Override
    protected int getNumberOfDays() {
	return 30;
    }

    @Override
    protected String startDate() {
	return "2021-03-01";
    }

    @Override
    protected EngineerType getType() {
	return EngineerType.Primary;
    }

    @Override
    protected EngineerFiles getRosterFile() {
	return EngineerFiles.MASTER_LIST;
    }

    @Override
    protected Predicate<OnlineScheduleEvent> getEventFilter() {

	return event -> {
	    if (event.getStartHour() == 0) {
		return false;
	    }

	    return !(event.getStartDayOfWeek() == Calendar.MONDAY && event.getStartHour() == 3);
	};
    }
}
