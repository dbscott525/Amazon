package com.scott_tigers.oncall.test;

import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Expertise;
import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	Stream.of("bogus", "MySQL8", "Serverless", "Binlog")
		.map(e -> Expertise.get(e))
		.forEach(e -> {
		    System.out.println("e=" + (e));
		    System.out.println("e.getNotation()=" + (e.getNotation()));
		    System.out.println("e.getRequiredOrder()=" + (e.getRequiredOrder()));
		});
    }
}