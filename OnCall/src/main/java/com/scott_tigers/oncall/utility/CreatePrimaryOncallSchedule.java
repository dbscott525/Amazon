package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreatePrimaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreatePrimaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	super.run();
    }

    @Override
    protected int getNumberOfDays() {
	return 10;
    }

    @Override
    protected String startDate() {
	return "2021-02-15";
    }

    @Override
    protected EngineerType getType() {
	return EngineerType.Primary;
    }

    @Override
    protected EngineerFiles getRosterFile() {
	return EngineerFiles.MASTER_LIST;
    }
}
