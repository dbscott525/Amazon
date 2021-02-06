package com.scott_tigers.oncall.shared;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;

public enum EngineerType {

    Primary {
	@Override
	int getStartHour() {
	    return 10;
	}

	@Override
	protected int getShiftHours() {
	    return 8;
	}

	@Override
	String getUrl() {
	    return "https://oncall.corp.amazon.com/#/view/aurora-head-primary/schedule";
	}

	@Override
	String getHistoricalUrl() {
	    return "https://oncall.corp.amazon.com/#/view/aurora-head-primary-ams/schedule";
	}
    },
    Secondary {
	@Override
	int getStartHour() {
	    return 10;
	}

	@Override
	protected int getShiftHours() {
	    return 24;
	}

	@Override
	String getUrl() {
	    return "https://oncall.corp.amazon.com/#/view/aurora-head-secondary/schedule";
	}
    },
    TechEsc {
	@Override
	public boolean isEngineer() {
	    return false;
	}

	@Override
	int getStartHour() {
	    return 10;
	}

	@Override
	protected int getShiftHours() {
	    return 24;
	}

	@Override
	String getUrl() {
	    return "https://oncall.corp.amazon.com/#/view/aurora-tech-escalation/schedule";
	}

	@Override
	public int getAdjustedTime(int time) {
	    return getStartHour();
	}
    };

    public OnCallSchedule getOnCallSchedule() {
	return new OnCallSchedule(this);
    }

    abstract String getUrl();

    public Stream<OnlineScheduleEvent> getOnCallScheduleStream() {
	return getScheduleStream(getUrl());
    }

    public Stream<OnlineScheduleEvent> getHistoricalOnCallScheduleStream() {
	return getScheduleStream(getHistoricalUrl());
    }

    private Stream<OnlineScheduleEvent> getScheduleStream(String url) {
	return getOnCallSchedule()
		.getOnCallScheduleList(url)
		.stream();
    }

    public boolean isEngineer() {
	return true;
    }

    public Stream<OnlineScheduleEvent> getStream(Consumer<ScheduleStream> parameterSetter) {
	ScheduleStream scheduleStream = new ScheduleStream();
	scheduleStream.type(this);
	parameterSetter.accept(scheduleStream);
	return scheduleStream.getStream();
    }

    abstract int getStartHour();

    OnlineScheduleEvent getEvent(String startDate, int startHour) {
	return new OnlineScheduleEvent(startDate, startHour, getShiftHours());

    }

    protected abstract int getShiftHours();

    OnlineScheduleEvent getEvent(String startDate) {
	return getEvent(startDate, getStartHour());

    }

    public int getAdjustedTime(int time) {
	return time;
    }

    public boolean engineerIsType(Engineer engineer) {
	return engineer.isType(this);
    }

    String getHistoricalUrl() {
	return getUrl();
    }
}
