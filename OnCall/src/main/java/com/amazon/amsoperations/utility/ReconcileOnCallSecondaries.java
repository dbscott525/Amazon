package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class ReconcileOnCallSecondaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallSecondaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(EngineerType.Secondary);
    }
}
