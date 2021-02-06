package com.scott_tigers.oncall.utility;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class UpdateEngineerMasterListWithOnCallStatus extends Utility {

    public static void main(String[] args) {
	new UpdateEngineerMasterListWithOnCallStatus().run();
    }

    private void run() {
	List<String> validTypes = Stream
		.of(EngineerType.values())
		.filter(EngineerType::isEngineer)
		.map(EngineerType::toString).collect(Collectors.toList());

	Map<String, String> oncallMap = Stream
		.of(EngineerType.values())
		.filter(EngineerType::isEngineer)
		.flatMap(type -> getOnCallUIDs(type)
			.stream()
			.map(uid -> new AbstractMap.SimpleEntry<String, String>(uid, type.toString())))
		.collect(Collectors.toMap(SimpleEntry<String, String>::getKey, SimpleEntry<String, String>::getValue));

	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();

	engineers
		.stream()
		.forEach(eng -> {
		    String type = Optional
			    .ofNullable(oncallMap.get(eng.getUid()))
			    .orElse(validTypes.contains(eng.getType()) ? "Former" + eng.getType() : eng.getType());
		    eng.setType(type);
		});

	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class));

    }
}
