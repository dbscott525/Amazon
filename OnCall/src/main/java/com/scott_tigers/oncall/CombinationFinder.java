package com.scott_tigers.oncall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CombinationFinder<T> {

    private static final int RESULT_SIZE = 3;
    private static final int LIST_SIZE = 4;

    private Integer resultSize;
    private List<T> inputList;
    private Consumer<List<T>> combinationHandler;

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
	cartesian(new ArrayList<T>(), inputList);
    }

    private void cartesian(List<T> prefix, List<T> postfix) {
	if ((resultSize != null && resultSize == prefix.size()) ||
		postfix.isEmpty()) {
	    if (combinationHandler != null) {
		combinationHandler.accept(prefix);
	    }
	    return;
	}

	IntStream.range(0, postfix.size()).forEach(index -> {

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

}
