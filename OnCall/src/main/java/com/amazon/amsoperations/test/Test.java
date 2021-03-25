package com.amazon.amsoperations.test;

import com.amazon.amsoperations.utility.LTTRPage;
import com.amazon.amsoperations.utility.Utility;

public class Test extends Utility {
    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	launchUrl(LTTRPage.GRAPH.getUrl());
    }

}