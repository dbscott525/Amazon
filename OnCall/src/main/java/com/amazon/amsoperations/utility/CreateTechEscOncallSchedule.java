package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class CreateTechEscOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateTechEscOncallSchedule().run();
    }

    protected void run() throws Exception {
	setEngineerType(EngineerType.TechEsc);
	super.run();
    }
}
