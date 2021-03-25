package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class CreateSecondaryEmailsFromOnCallSchedule extends CreateEmailsFromOnCallSchedule {

    public static void main(String[] args) {
	new CreateSecondaryEmailsFromOnCallSchedule().run();
    }

    protected void run() {
	super.run();
    }

    @Override
    protected EngineerType getOnCallType() {
	return EngineerType.Secondary;
    }

}
