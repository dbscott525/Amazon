package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Oncall;

public class ReconcileOnCallSecondaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallSecondaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(Oncall.Secondary, EngineerType.Secondary);
    }
}
