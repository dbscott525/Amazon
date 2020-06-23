package com.scott_tigers.oncall;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public enum ScheduleType {

    ON_CALL {
	@Override
	int getRotationSize() {
	    return 3;
	}

	@Override
	protected int getDaysPerInterval() {
	    return 1;
	}

	@Override
	protected Predicate<Engineer> getEngineerFilter() {
	    return engineer -> true;
	}
    },
    PRIORITY {
	@Override
	int getRotationSize() {
	    return 3;
	}

	@Override
	protected int getDaysPerInterval() {
	    return 7;
	}

	@Override
	protected Predicate<Engineer> getEngineerFilter() {
	    return engineer -> engineer.isGreaterThanPercentile(60);
	}
    };

    Schedule getSchedue(Date startDate, List<Engineer> engineers) {
	return new Scheduler(this, startDate, engineers).getSchedule();
    }

    protected abstract int getRotationSize();

    protected abstract int getDaysPerInterval();

    protected abstract Predicate<? super Engineer> getEngineerFilter();
}
