package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class ShowTechEscOncallScheduleReport extends ShowOncallScheduleReport {

    public static void main(String[] args) {
	new ShowTechEscOncallScheduleReport().run();
    }

    protected void run() {
	setEngineeringType(EngineerType.TechEsc);
	super.run();
    }
}
