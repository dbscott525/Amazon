package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class ReconcileOnCallPrimaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallPrimaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(EngineerType.Primary);
    }

}
