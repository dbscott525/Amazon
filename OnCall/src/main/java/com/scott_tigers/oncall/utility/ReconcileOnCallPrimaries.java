package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Oncall;

public class ReconcileOnCallPrimaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallPrimaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(Oncall.Primary, EngineerType.Primary);
    }

}
