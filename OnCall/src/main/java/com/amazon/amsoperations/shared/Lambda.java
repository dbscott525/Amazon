package com.amazon.amsoperations.shared;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lambda {

    public static <T> Predicate<List<T>> minSize(int minimum) {
        return elements -> elements.size() >= minimum;
    }

    public static <T> Function<List<T>, T> getElement(int index) {
        return list -> list.get(index);
    }

}
