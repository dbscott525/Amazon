package com.amazon.amsoperations.bean;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface TTReader {
    public String getUrl();

    public Predicate<TT> getFilter();

    public String getTitle();

    public void printReport();

    public Stream<TT> getTicketStream() throws Exception;

}
