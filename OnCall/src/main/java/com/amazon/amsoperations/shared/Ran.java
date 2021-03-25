package com.amazon.amsoperations.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ran {

    public static <T> Collector<T, List<T>, Stream<T>> toStream() {
        BiConsumer<List<T>, T> accumulator = (list, element) -> list.add(element);
        BinaryOperator<List<T>> combiner = (list1, list2) -> Stream
        	.of(list1, list2)
        	.flatMap(List<T>::stream)
        	.collect(Collectors.toList());
        Supplier<List<T>> supplier = () -> new ArrayList<T>();
        Function<List<T>, Stream<T>> finalizer = list -> {
            Collections.shuffle(list);
            return list.stream();
        };
    
        return Collector.of(supplier, accumulator, combiner, finalizer);
    
    }

}
