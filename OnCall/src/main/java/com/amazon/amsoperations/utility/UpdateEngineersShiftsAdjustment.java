package com.amazon.amsoperations.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.EngineerFiles;

public class UpdateEngineersShiftsAdjustment extends Utility implements Command {

    private Map<String, Engineer> egnineerMap;

    public static void main(String[] args) throws Exception {
	new UpdateEngineersShiftsAdjustment().run();
    }

    @Override
    public void run() throws Exception {
	List<Engineer> engineers = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.collect(Collectors.toList());

	egnineerMap = engineers.stream().collect(Collectors.toMap(Engineer::getUid, x -> x));

	engineers.stream()
		.filter(Engineer::isCurrentCit)
		.sorted(Comparator.comparing(Engineer::getSortableStartDate))
		.forEach(eng -> {
		    Map<String, Long> shiftCountMap = getShiftCountMap(eng);

		    updateMapWithAllowances(shiftCountMap);

		    double average = getAverageNumberOfShifts(shiftCountMap);

		    eng.setShiftAllowance((int) Math.round(average));
		});

	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class).noOpen());
    }

    private double getAverageNumberOfShifts(Map<String, Long> shiftCountMap) {
	return shiftCountMap.values()
		.stream()
		.mapToDouble(x -> x)
		.average()
		.orElse(0);
    }

    private void updateMapWithAllowances(Map<String, Long> shiftCountMap) {
	shiftCountMap
		.entrySet()
		.stream()
		.forEach(entry -> {
		    Engineer engineer = egnineerMap.get(entry.getKey());
		    entry.setValue(entry.getValue()
			    + (engineer == null ? 0 : engineer.getShiftAllowance()));
		});
    }

    private Map<String, Long> getShiftCountMap(Engineer eng) {
	return getShiftStream()
		.filter(shift -> shift.isBefore(eng.getSortableStartDate()))
		.flatMap(shift -> shift.getUids().stream())
		.filter(uid -> Objects.nonNull(egnineerMap.get(uid)))
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}