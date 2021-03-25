package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.function.Predicate;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.TimeZone;

public class CreateTraineeOncallSchedule extends CreateOncallJsonSchedule {

//    private static final String END_DATE = "2021-04-05";
//    private static final String START_DATE = "2021-03-29";
    private static final String END_DATE = "2021-06-04";
    private static final String START_DATE = "2021-04-05";
//    private static final int NUMBER_OF_WEEKS = 12;

    public static void main(String[] args) throws Exception {
	new CreateTraineeOncallSchedule().run();
    }

    protected void run() throws Exception {
	eventFilter(getEventFilter());
	doNotUseHistory();
	startDate(START_DATE);
	endDate(END_DATE);
	setEngineerType(EngineerType.Trainee);
	allowGaps();
//	setWeeks(NUMBER_OF_WEEKS);
	noSameWeekEvents();
	noScheduleGapFilter();
	super.run();
    }

    private Predicate<OnlineScheduleEvent> getEventFilter() {
	return event -> {
	    switch (event.getStartDayOfWeek()) {

	    case Calendar.MONDAY:
		return !event.getTimeZone().equals(TimeZone.EST);

	    case Calendar.SUNDAY:
	    case Calendar.SATURDAY:
		return false;

	    case Calendar.FRIDAY:
		return event.getStartHour() <= 15;

	    default:
		return true;

	    }
	};
    }

}
