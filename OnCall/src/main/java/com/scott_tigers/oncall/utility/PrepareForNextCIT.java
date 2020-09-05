package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

public class PrepareForNextCIT extends Utility {

    public static void main(String[] args) {
	new PrepareForNextCIT().run();
    }

    private void run() {
	Stream<Class<? extends Command>> x1 = Stream.of(CreateCITEmails.class, CreateOncallSchedule.class,
		CreateDailyOnCallReminderEmails.class);
	Stream<Command> x2 = x1.map(c -> constuct(c));
	x2.forEach(command -> {
	    try {
		command.run();
	    } catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	    }
	});
    }

}
