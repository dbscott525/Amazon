package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

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
