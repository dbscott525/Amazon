package com.scott_tigers.oncall.utility;

import java.io.IOException;

public class CreateCSVSchedule extends Utility implements Command {

    public static void main(String[] args) throws IOException {
	new CreateCSVSchedule().run();
    }

    @Override
    public void run() throws IOException {
	createCSVCITSchedule();
    }
}
