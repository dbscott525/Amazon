package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class ShowTechEscOncallScheduleReport extends ShowOncallScheduleReport {

    public static void main(String[] args) {
	new ShowTechEscOncallScheduleReport().run();
    }

    protected void run() {
	setEngineeringType(EngineerType.TechEsc);
	super.run();
    }
}
