package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class CreatePrimaryEmailsFromOnCallSchedule extends CreateEmailsFromOnCallSchedule {

    public static void main(String[] args) {
	new CreatePrimaryEmailsFromOnCallSchedule().run();
    }

    protected void run() {
	super.run();
    }

    @Override
    protected EngineerType getOnCallType() {
	return EngineerType.Primary;
    }

}
