package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class CreateSecondaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateSecondaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	setEngineerType(EngineerType.Secondary);
	super.run();
    }
}
