package com.scott_tigers.oncall;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public enum ScheduleType {

    ON_CALL {
	@Override
	protected int getRotationSize() {
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

	@Override
	protected List<BiConsumer<ScheduleRow, String>> getEngineerMaps() {
	    return Arrays.asList(
		    (row, name) -> row.setOnCallLead(name),
		    (row, name) -> row.setOnCall2(name),
		    (row, name) -> row.setOnCall3(name));
	}
    },
    PRIORITY {
	@Override
	protected int getRotationSize() {
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

	@Override
	protected List<BiConsumer<ScheduleRow, String>> getEngineerMaps() {
	    return null;
	}

    };

    protected abstract int getRotationSize();

    protected abstract int getDaysPerInterval();

    protected abstract Predicate<? super Engineer> getEngineerFilter();

    protected abstract List<BiConsumer<ScheduleRow, String>> getEngineerMaps();

    Scheduler build() {
	return new Scheduler(this);
    }
}
