package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class CreateTechEscOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateTechEscOncallSchedule().run();
    }

    protected void run() throws Exception {
	setEngineerType(EngineerType.TechEsc);
	super.run();
    }
}
