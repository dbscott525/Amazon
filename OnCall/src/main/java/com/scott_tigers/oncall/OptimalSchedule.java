package com.scott_tigers.oncall;

import java.util.Date;
import java.util.List;

public class OptimalSchedule {

    private List<Engineer> engineers;
    private Schedule bestSchedule = null;
    private Date startDate;

    public OptimalSchedule(List<Engineer> engineers, Date startDate) {
	this.engineers = engineers;
	this.startDate = startDate;
    }

    public Schedule getSchedule() {

	Engineer engineerArr[] = (engineers.stream().toArray(Engineer[]::new));
	var engineersInSchedule = engineers.size() / Constants.ON_CALLS_PER_DAY
		* Constants.ON_CALLS_PER_DAY;

	Engineer resultCombination[] = new Engineer[engineersInSchedule];

	searchForSchedule(engineerArr, resultCombination, 0, engineerArr.length - 1, 0, engineersInSchedule);
	return bestSchedule;

    }

    void searchForSchedule(
	    Engineer inputEngineers[],
	    Engineer outputEngineers[],
	    int start,
	    int end,
	    int index,
	    int combinationSize) {

	if (index == combinationSize) {
	    bestSchedule = new Schedule(outputEngineers, startDate).getBestSchedule(bestSchedule);
	    return;
	}

	for (int i = start; i <= end && end - i + 1 >= combinationSize - index; i++) {
	    outputEngineers[index] = inputEngineers[i];
	    searchForSchedule(inputEngineers, outputEngineers, i + 1, end, index + 1, combinationSize);
	}
    }
}
