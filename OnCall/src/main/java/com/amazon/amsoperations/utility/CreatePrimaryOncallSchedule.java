package com.amazon.amsoperations.utility;

import java.util.Calendar;
import java.util.function.Predicate;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.shared.EngineerType;

public class CreatePrimaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreatePrimaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	eventFilter(getEventFilter());
	setEngineerType(EngineerType.Primary);
	postScheduleProcess(schedule -> mergeOnCallTrainees(schedule));
	super.run();

    }

    private Predicate<OnlineScheduleEvent> getEventFilter() {
	return event -> {
	    switch (event.getStartHour()) {

	    case 0: // IST is a separate schedule
		return false;

	    case 3: // Monday at 3 AM PST is for Dublin
		return event.getStartDayOfWeek() != Calendar.MONDAY;

	    default:
		return true;
	    }
	};
    }

}
