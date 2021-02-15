package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.utility.LTTRWeeks;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	Json.print(LTTRWeeks.get());
	Json.print(LTTRWeeks.get());
    }

}