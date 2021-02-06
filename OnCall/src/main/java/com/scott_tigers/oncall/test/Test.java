package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	EngineerFiles.ON_CALL_DAILY_REMINDER_EMAIL.launch();
    }

}