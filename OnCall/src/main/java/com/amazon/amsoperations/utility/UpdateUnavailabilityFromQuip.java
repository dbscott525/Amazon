package com.amazon.amsoperations.utility;

import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Unavailability;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

public class UpdateUnavailabilityFromQuip extends Utility implements Command {

    public static void main(String[] args) {
	new UpdateUnavailabilityFromQuip().run();
    }

    @Override
    public void run() {
	EngineerFiles.UNAVAILABILITY
		.write(w -> w.CSV(
			readFromUrl(
				URL.CIT_UNAVAILABILITY,
				Unavailability.class)
					.collect(Collectors.toList()),
			Unavailability.class));
    }

}
