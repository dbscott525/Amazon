package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

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
