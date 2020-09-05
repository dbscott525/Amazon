package com.scott_tigers.oncall.utility;

import java.io.IOException;

public class CreateCSVSchedule extends Utility {

    public static void main(String[] args) throws IOException {
	new CreateCSVSchedule().run();
    }

    private void run() throws IOException {
	createCSVCITSchedule();
    }
}
