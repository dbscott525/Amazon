package com.scott_tigers.oncall.bean;

import java.util.function.Predicate;

public interface TTReader {
    public String getUrl();

    public Predicate<TT> getFilter();

}
