package com.scott_tigers.oncall.utility;

public class PrepareForWeeklyOpsReview extends Utility {

    public static void main(String[] args) {
	new PrepareForWeeklyOpsReview().run();
    }

    @SuppressWarnings("unchecked")
    private void run() {
	runCommands(

		UpdateLttrCandidateFrequencies.class,
		UpdateLttrPlanFrequencies.class,
		CreateLttrWeekDeltaReport.class

	);
    }

}
