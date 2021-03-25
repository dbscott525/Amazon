package com.amazon.amsoperations.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.amazon.amsoperations.shared.ShadowSlot;

public class TrainingDaySchedule {

    private String schedule;

    public TrainingDaySchedule(String date, List<Engineer> engineers) {
	ArrayList<Engineer> schedule = new ArrayList<>(engineers);

	boolean found = false;
	int tries = 0;

	while (!found) {
	    if (tries > 1000) {
		System.out.println("too many tries");
		System.exit(1);
	    }
	    tries++;
	    Collections.shuffle(schedule);
	    found = IntStream.range(0, schedule.size()).allMatch(index -> ShadowSlot
		    .values()[index / 2]
			    .isCompatible(schedule.get(index)));
	}

	this.schedule = Stream
		.of(Stream.of(date), schedule.stream().map(t -> t.getFullNameWithUid()))
		.flatMap(x -> x)
		.collect(Collectors.joining(","));

    }

    public String getSchedule() {
	return schedule;
    }

}
