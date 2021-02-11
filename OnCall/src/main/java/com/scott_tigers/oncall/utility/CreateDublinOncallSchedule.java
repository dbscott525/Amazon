package com.scott_tigers.oncall.utility;

import java.util.function.Predicate;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateDublinOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateDublinOncallSchedule().run();
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
	return "2021-02-22";
    }

    @Override
    protected EngineerType getType() {
	return EngineerType.DublinPrimary;
    }

    @Override
    protected EngineerFiles getRosterFile() {
	return EngineerFiles.MASTER_LIST;
    }

    @Override
    protected Predicate<OnlineScheduleEvent> getEventFilter() {
	return this::isDublinSchedule;
    }
}
