package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class ShowPrimaryOncallScheduleReport extends ShowOncallScheduleReport {

    public static void main(String[] args) {
	new ShowPrimaryOncallScheduleReport().run();
    }

    protected void run() {
	setEngineeringType(EngineerType.Primary);
//	useSavedSchedule();
	super.run();
    }
}
