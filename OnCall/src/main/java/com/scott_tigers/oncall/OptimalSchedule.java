package com.scott_tigers.oncall;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OptimalSchedule {

    private List<Engineer> engineers;

    public OptimalSchedule(List<Engineer> engineers) {
	this.engineers = engineers;
    }
    //

    public void getSchedule() {

	Engineer engineerArr[] = (engineers.stream().toArray(Engineer[]::new));
	var engineersInSchedule = engineers.size() / Constants.ON_CALLS_PER_DAY
		* Constants.ON_CALLS_PER_DAY;
	// A temporary array to store all combination one by one
	Engineer resultCombination[] = new Engineer[engineersInSchedule];

	combinationUtil(engineerArr, resultCombination, 0, engineerArr.length - 1, 0, engineersInSchedule);

    }

    static void combinationUtil(
	    Engineer inputEngineers[],
	    Engineer outputEngineers[],
	    int start,
	    int end,
	    int index,
	    int combinationSize) {

	if (index == combinationSize) {
	    String engs = Arrays.stream(outputEngineers).map(e -> e.getName())
		    .collect(Collectors.joining("/"));
	    System.out.println("engs=" + (engs));

	    int days = combinationSize / Constants.ON_CALLS_PER_DAY;
	    int[] levelAggregates = IntStream
		    .range(0, outputEngineers.length).mapToObj(x -> Integer.valueOf(x))
		    .collect(dayLevelCollector(outputEngineers, days));
	    String levels = Arrays.stream(levelAggregates).mapToObj(level -> "" + level)
		    .collect(Collectors.joining("/"));
	    System.out.println("levels=" + (levels));
	    return;
	}

	for (int i = start; i <= end && end - i + 1 >= combinationSize - index; i++) {
	    outputEngineers[index] = inputEngineers[i];
	    combinationUtil(inputEngineers, outputEngineers, i + 1, end, index + 1, combinationSize);
	}
    }

    private static Collector<Object, int[], int[]> dayLevelCollector(
	    Engineer[] arr, int days) {
	Supplier<int[]> supplier = () -> new int[days];
	BiConsumer<int[], Object> accumlator = (result,
		index) -> result[(int) index / Constants.ON_CALLS_PER_DAY] += arr[(int) index].getLevel();
	BinaryOperator<int[]> combiner = (result1, result2) -> IntStream
		.range(0, result1.length)
		.map(i -> result1[i] + result2[i]).toArray();
	return Collector.of(supplier, accumlator, combiner);
    }
}
