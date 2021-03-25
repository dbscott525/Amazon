package com.amazon.amsoperations.shared;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.KeywordPoints;
import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.utility.WeightContext;

public enum TicketType {

    CustomerIssue {
	@Override
	public Integer getMaximumTickets() {
	    return Constants.CUSTOMER_ISSUE_TICKETS_ON_THE_QUEUE;
	}

	@Override
	public int getWeight(WeightContext weightContext) {

	    TT tt = weightContext.getTt();

	    Supplier<Integer> status = () -> Status.get(tt.getStatus()).getWeight();
	    Supplier<Integer> age = () -> (int) Math.pow(weightContext.getAge(), Constants.CUSTOMER_ISSUE_AGE_EXPONENT);
	    Supplier<Integer> impact = () -> (int) Math.pow(6 - tt.getImpact(), 3);

	    Stream<Integer> other = Stream
		    .of(status, age, impact)
		    .map(Supplier<Integer>::get);

	    Stream<Integer> keyword = weightContext
		    .getKeywordPoints()
		    .stream()
		    .filter(keywordMatch(tt.getDescription()))
		    .map(KeywordPoints::getPoints);

	    return Stream
		    .concat(other, keyword)
		    .mapToInt(Integer::intValue)
		    .sum();
	}
    },

    Engine {
	@Override
	public Integer getMaximumTickets() {
	    return Constants.ENGINE_TICKETS_ON_THE_QUEUE;
	}

	@Override
	public int getWeight(WeightContext weightContext) {
	    return (int) Math.pow(weightContext.getAge(), weightContext.getEngineTypeExponent());
	}
    },

    Invalid {
	@Override
	public Integer getMaximumTickets() {
	    return 0;
	}

	@Override
	public int getWeight(WeightContext weightContext) {
	    return 0;
	}
    };

    public static TicketType getType(String value) {
	try {
	    return valueOf(value);
	} catch (Exception e) {
	    return Invalid;
	}
    }

    private static Predicate<KeywordPoints> keywordMatch(String description) {
	return kw -> Util.foundIn(description, kw.getKeyword());
    }

    public abstract Integer getMaximumTickets();

    public abstract int getWeight(WeightContext weightContext);

}
