package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.URL;

public class UpdateUnavailabilityFromQuip extends Utility {

    public static void main(String[] args) {
	new UpdateUnavailabilityFromQuip().run();
    }

    private void run() {
	EngineerFiles.UNAVAILABILITY
		.write(w -> w.CSV(
			readFromUrl(
				URL.CIT_UNAVAILABILITY,
				Unavailability.class)
					.collect(Collectors.toList()),
			Unavailability.class));
    }

}
