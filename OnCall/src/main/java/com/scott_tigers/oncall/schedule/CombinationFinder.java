package com.scott_tigers.oncall.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.Executor;

public class CombinationFinder<T> {

//    interface Executer {
//	void check();
//    }

    private static final int RESULT_SIZE = 3;
    private static final int LIST_SIZE = 4;

    private Integer resultSize;
    private List<T> inputList;
    private Consumer<List<T>> combinationHandler = list -> {
    };
    private Function<List<T>, Boolean> prefixChecker = list -> true;
    private Executor timeOut = () -> {
    };
    private long startTimeInMillis;
    private int iterationCounter;

    public static void main(String[] args) {
	List<String> inputList = IntStream
		.range(0, LIST_SIZE)
		.mapToObj(x -> Character.toString((char) ('A' + x)))
		.collect(Collectors.toList());

	new CombinationFinder<String>()
		.input(inputList).resultSize(RESULT_SIZE)
		.combinationHandler(combination -> System.out.println("combination=" + (combination)))
		.generate();
    }

    public CombinationFinder<T> combinationHandler(Consumer<List<T>> combinationHandler) {
	this.combinationHandler = combinationHandler;
	return this;
    }

    public CombinationFinder<T> resultSize(int resultSize) {
	this.resultSize = resultSize;
	return this;
    }

    public CombinationFinder<T> input(List<T> inputList) {
	this.inputList = inputList;
	return this;
    }

    public void generate() {
	startTimeInMillis = System.currentTimeMillis();
	try {
	    cartesian(new ArrayList<T>(), inputList);
	} catch (Exception e) {
	    System.out.println("SEARCH TIMED OUT");
	}
    }

    private void cartesian(List<T> prefix, List<T> postfix) {

	timeOut.run();

	if (!prefixChecker.apply(prefix)) {
	    return;
	}

	if (postfix.isEmpty() || prefix.size() >= resultSize) {
	    combinationHandler.accept(prefix);
	    return;
	}

	IntStream.range(0, postfix.size())
//		.parallel()
		.forEach(index -> {
		    timeOut.run();

		    List<T> newPrefix = Stream
			    .of(prefix, Arrays.asList(postfix.get(index)))
			    .flatMap(List<T>::stream)
			    .collect(Collectors.toList());

		    List<T> newPostfix = IntStream
			    .range(0, postfix.size())
			    .filter(i -> i != index)
			    .mapToObj(postfix::get)
			    .collect(Collectors.toList());

		    cartesian(newPrefix, newPostfix);

		});
    }

    public CombinationFinder<T> prefixChecker(Function<List<T>, Boolean> prefixChecker) {
	this.prefixChecker = prefixChecker;
	return this;
    }

    public CombinationFinder<T> timeLimit(int timeLimit) {
	long maximumElapsedMillis = TimeUnit.MINUTES.toMillis(timeLimit);
	timeOut = () -> {
	    if (++iterationCounter > 1000000) {
		long elapsed = System.currentTimeMillis() - startTimeInMillis;
		iterationCounter = 0;
		if (elapsed > maximumElapsedMillis) {
		    throw new RuntimeException(new Exception("TIMED OUT IN CARTESIAN"));
		}
	    }
	    return;
	};
	return this;
    }
}
