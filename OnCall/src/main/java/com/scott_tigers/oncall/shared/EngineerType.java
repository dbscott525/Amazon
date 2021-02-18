package com.scott_tigers.oncall.shared;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;

public enum EngineerType {

    Primary {
	@Override
	public int getStartHour() {
	    return 0;
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

	@Override
	public Iterator<ScheduleType> getScheduleTypeIterator() {
	    return new ScheduleTypeIterator(i -> i
		    .add(TimeZone.IST, 3)
		    .add(TimeZone.EST, 6)
		    .add(TimeZone.PST, 5)
		    .add(TimeZone.PST, 5)
		    .add(TimeZone.PST, 5));
	}

	@Override
	public boolean useTimeZones() {
	    return true;
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.PRIMARY_ONCALL_SCHEDULE;
	}

    },
    Secondary {
	@Override
	public int getStartHour() {
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

	@Override
	public Iterator<ScheduleType> getScheduleTypeIterator() {
	    return new ScheduleTypeIterator(i -> i.add(TimeZone.PST, 24));
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.SECONDARY_ONCALL_SCHEDULE;
	}

    },
    TechEsc {
	@Override
	public boolean isEngineer() {
	    return false;
	}

	@Override
	public int getStartHour() {
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

	@Override
	public Iterator<ScheduleType> getScheduleTypeIterator() {
	    return new ScheduleTypeIterator(i -> i.add(TimeZone.PST, 24));
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.TECH_ESC_ONCALL_SCHEDULE;
	}
    },
    DublinPrimary {
	@Override
	String getHistoricalUrl() {
	    return "https://oncall.corp.amazon.com/#/view/aurora-data-lake-primary/schedule";
	}

	@Override
	String getUrl() {
	    return Primary.getUrl();
	}

	@Override
	public int getStartHour() {
	    return 0;
	}

	@Override
	protected int getShiftHours() {
	    return 0;
	}

	@Override
	public Iterator<ScheduleType> getScheduleTypeIterator() {
	    return new ScheduleTypeIterator(i -> i
		    .add(TimeZone.IST, 3)
		    .add(TimeZone.DUB, 6)
		    .add(TimeZone.PST, 15));
	}

	@Override
	public boolean useTimeZones() {
	    return true;
	}

	@Override
	public boolean useForDailyBulletin() {
	    return false;
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    // TODO Auto-generated method stub
	    return null;
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

    public abstract int getStartHour();

    protected abstract int getShiftHours();

    OnlineScheduleEvent getEvent(String startDate) {
	return new OnlineScheduleEvent(startDate, this, getStartHour(), getShiftHours());

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

    public abstract Iterator<ScheduleType> getScheduleTypeIterator();

    public boolean useTimeZones() {
	return false;
    }

    public boolean useForDailyBulletin() {
	return true;
    }

    public abstract EngineerFiles getScheduleFile();
}
