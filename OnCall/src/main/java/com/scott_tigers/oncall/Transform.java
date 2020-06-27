package com.scott_tigers.oncall;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Transform {

    public static <I, O> List<O> list(List<I> inputList, Function<Stream<I>, Stream<O>> transformer) {

	return transformer
		.apply(inputList.stream())
		.collect(Collectors.toList());

    }

}
