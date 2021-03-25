package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.utility.LTTRPage;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {
    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	launchUrl(LTTRPage.GRAPH.getUrl());
    }

}