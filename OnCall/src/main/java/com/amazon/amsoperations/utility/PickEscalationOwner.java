package com.amazon.amsoperations.utility;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.EngineerFiles;

public class PickEscalationOwner extends Utility {

    public static void main(String[] args) {
	new PickEscalationOwner().run();
    }

    private Random random;

    private void run() {
	random = new Random();

	Map<String, EscalationCount> escalationMap = EngineerFiles.SDMS
		.readCSV()
		.stream()
		.map(EscalationCount::new)
		.collect(Collectors.toMap(EscalationCount::getFirstName, x -> x));

	EngineerFiles.ESCALATION_OWNERSHIP.readCSV()
		.stream()
		.map(Engineer::getFirstName)
		.map(escalationMap::get)
		.forEach(EscalationCount::incrementEscalations);

	EngineerFiles.ESCALATION_CHOOSER.writeCSV(escalationMap
		.entrySet()
		.stream()
		.map(Entry<String, EscalationCount>::getValue)
		.sorted()
		.collect(Collectors.toList()), EscalationCount.class);

	successfulFileCreation(EngineerFiles.ESCALATION_CHOOSER);

    }

    private class EscalationCount implements Comparable<EscalationCount> {
	private String firstName;
	private int escalationCount;
	private int randomKey;

	public EscalationCount(Engineer engineer) {
	    firstName = engineer.getFirstName();
	    randomKey = random.nextInt();
	}

	public void incrementEscalations() {
	    escalationCount++;
	}

	public String getFirstName() {
	    return firstName;
	}

	@Override
	public int compareTo(EscalationCount o) {
	    if (escalationCount == o.escalationCount) {
		return randomKey - o.randomKey;
	    }
	    return escalationCount - o.escalationCount;
	}

	@Override
	public String toString() {
	    return "EscalationCount [firstName=" + firstName + ", escalationCount=" + escalationCount + ", randomKey="
		    + randomKey + "]";
	}

    }

}
