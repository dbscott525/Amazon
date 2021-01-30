package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.Oncall;

public class CreateSecondaryEmailsFromOnCallSchedule extends CreateEmailsFromOnCallSchedule {

    public static void main(String[] args) {
	new CreateSecondaryEmailsFromOnCallSchedule().run();
    }

    protected void run() {
	super.run();
    }

    @Override
    protected Oncall getOnCallType() {
	return Oncall.Secondary;
    }

}
