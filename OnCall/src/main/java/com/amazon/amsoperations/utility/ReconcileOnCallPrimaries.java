package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.EngineerType;

public class ReconcileOnCallPrimaries extends Utility {

    public static void main(String[] args) {
	new ReconcileOnCallPrimaries().run();
    }

    private void run() {
	reconcileMasterListToOncallList(EngineerType.Primary);
    }

}
