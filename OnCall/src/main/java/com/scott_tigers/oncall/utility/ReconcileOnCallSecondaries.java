package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;

public class ReconcileOnCallSecondaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallSecondaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(EngineerType.Secondary);
    }
}
