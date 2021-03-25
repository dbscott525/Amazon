package com.amazon.amsoperations.shared;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.bean.OnlineScheduleEvent;

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
	    if (AFTER_2021_04_04) {
		return new ScheduleTypeIterator(i -> i
			.add(TimeZone.IST, 4)
			.add(TimeZone.EST, 5)
			.add(TimeZone.PST, 8)
			.add(TimeZone.PST, 7));

	    } else {
		return new ScheduleTypeIterator(i -> i
			.add(TimeZone.IST, 3)
			.add(TimeZone.EST, 6)
			.add(TimeZone.PST, 8)
			.add(TimeZone.PST, 7));

	    }
	}

	@Override
	public boolean useTimeZones() {
	    return true;
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.PRIMARY_ONCALL_SCHEDULE;
	}

	@Override
	public boolean isTimeZoneSensitive() {
	    return true;
	}

	@Override
	public EngineerFiles getScheduleContainerFiler() {
	    return EngineerFiles.PRIMARY_ONCALL_SCHEDULE_CONTAINER;
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

	@Override
	public boolean isTimeZoneSensitive() {
	    return false;
	}

	@Override
	public EngineerFiles getScheduleContainerFiler() {
	    return EngineerFiles.SECONDARY_ONCALL_SCHEDULE_CONTAINER;
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
	public Iterator<ScheduleType> getScheduleTypeIterator() {
	    return new ScheduleTypeIterator(i -> i.add(TimeZone.PST, 24));
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.TECH_ESC_ONCALL_SCHEDULE;
	}

	@Override
	public boolean isTimeZoneSensitive() {
	    return false;
	}

	@Override
	public EngineerFiles getScheduleContainerFiler() {
	    return EngineerFiles.TECH_ESC_ONCALL_SCHEDULE_CONTAINER;
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
	    return null;
	}

	@Override
	public boolean isTimeZoneSensitive() {
	    return false;
	}

	@Override
	public EngineerFiles getScheduleContainerFiler() {
	    // TODO Auto-generated method stub
	    return null;
	}
    },
    Trainee {
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
	    if (AFTER_2021_04_04) {
		return new ScheduleTypeIterator(i -> i
			.add(TimeZone.SKIP, 4)
			.add(TimeZone.EST, 5)
			.add(TimeZone.PST, 8)
			.add(TimeZone.SKIP, 7));
	    } else {
		return new ScheduleTypeIterator(i -> i
			.add(TimeZone.SKIP, 3)
			.add(TimeZone.EST, 6)
			.add(TimeZone.PST, 8)
			.add(TimeZone.SKIP, 7));
	    }
	}

	@Override
	public boolean useTimeZones() {
	    return true;
	}

	@Override
	public EngineerFiles getScheduleFile() {
	    return EngineerFiles.TRAINEE_ONCALL_SCHEDULE;
	}

	@Override
	public boolean isTimeZoneSensitive() {
	    return true;
	}

	@Override
	public EngineerFiles getScheduleContainerFiler() {
	    return EngineerFiles.TRAINEE_ONCALL_SCHEDULE_CONTAINER;
	}

    };

    private static final boolean AFTER_2021_04_04 = true;

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

    public abstract boolean isTimeZoneSensitive();

    public abstract EngineerFiles getScheduleContainerFiler();
}
