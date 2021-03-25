package com.amazon.amsoperations.shared;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class IteratorStream<T> {
    public Stream<T> getStream() {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<T>() {

			    @Override
			    public boolean hasNext() {
				return IteratorStream.this.hasNext();
			    }

			    @Override
			    public T next() {
				return IteratorStream.this.next();
			    }
			},
			Spliterator.ORDERED),
		false);
    }

    protected abstract T next();

    protected abstract boolean hasNext();
}
