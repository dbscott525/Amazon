package com.scott_tigers.oncall;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ResultCache<Key, Result> {

    private Map<Key, Result> cache = new HashMap<Key, Result>();

    public Result evaluate(Key key, Supplier<Result> supplier) {
	Result result = cache.get(key);
	if (result == null) {
	    result = supplier.get();
	    cache.put(key, result);
	}
	return result;
    }

}
