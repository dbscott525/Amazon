package com.scott_tigers.oncall.test;

import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	String input = "08/17/2020,Suhas,0,https://tt.amazon.com/0506731372,Rackspace,This is a reported issued for which we do not have a fix at this point; https://sim.amazon.com/issues/AURORA-16920 ; Similar comments for another customer https://tt.amazon.com/0508328671,1,0";
//	String regex = ".*?https:\\.*?com/([0-9]+).*";
	String regex = ".*?https://tt\\.amazon\\.com/([0-9]+).*";
	String result = input.replaceAll(regex, "$1");
	System.out.println("result=" + (result));
    }
}