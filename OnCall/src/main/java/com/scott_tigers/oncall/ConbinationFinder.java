package com.scott_tigers.oncall;

import java.util.function.Consumer;

public class ConbinationFinder {

    private Consumer<Engineer[]> scheduleHandler;

    public ConbinationFinder(Engineer[] engineers, Consumer<Engineer[]> scheduleHandler, int slots) {
	this.scheduleHandler = scheduleHandler;
	searchForCombination(engineers, new Engineer[slots], 0, engineers.length - 1, 0, slots);
    }

    void searchForCombination(
	    Engineer inputEngineers[],
	    Engineer outputEngineers[],
	    int start,
	    int end,
	    int index,
	    int combinationSize) {

	if (index == combinationSize) {
	    scheduleHandler.accept(outputEngineers);
	    return;
	}

	for (int i = start; i <= end && end - i + 1 >= combinationSize - index; i++) {
	    outputEngineers[index] = inputEngineers[i];
	    searchForCombination(inputEngineers, outputEngineers, i + 1, end, index + 1, combinationSize);
	}
    }
}
