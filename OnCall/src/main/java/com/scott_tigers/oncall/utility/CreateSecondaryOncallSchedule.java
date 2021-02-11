package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateSecondaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateSecondaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	super.run();
    }

    @Override
    protected int getNumberOfDays() {
	return 60;
    }

    @Override
    protected String startDate() {
	return "2021-02-22";
    }

    @Override
    protected EngineerType getType() {
	return EngineerType.Secondary;
    }

    @Override
    protected EngineerFiles getRosterFile() {
	return EngineerFiles.MASTER_LIST;
    }
}
