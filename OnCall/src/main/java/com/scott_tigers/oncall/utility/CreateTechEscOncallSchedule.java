package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateTechEscOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateTechEscOncallSchedule().run();
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
	return "2021-03-08";
    }

    @Override
    protected EngineerType getType() {
	return EngineerType.TechEsc;
    }

    @Override
    protected EngineerFiles getRosterFile() {
	return EngineerFiles.TECH_ESC;
    }
}
