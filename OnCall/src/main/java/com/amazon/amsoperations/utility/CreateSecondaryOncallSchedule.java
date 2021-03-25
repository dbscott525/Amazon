package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class CreateSecondaryOncallSchedule extends CreateOncallJsonSchedule {

    public static void main(String[] args) throws Exception {
	new CreateSecondaryOncallSchedule().run();
    }

    protected void run() throws Exception {
	setEngineerType(EngineerType.Secondary);
	super.run();
    }
}
